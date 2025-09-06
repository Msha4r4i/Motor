package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.CarTransferResponseDTO;
import com.fkhrayef.motor.Model.Car;
import com.fkhrayef.motor.Model.CarTransferRequest;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.CarRepository;
import com.fkhrayef.motor.Repository.CarTransferRequestRepository;
import com.fkhrayef.motor.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CarTransferRequestService {

    private final CarTransferRequestRepository transferRepo;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    private static final String PENDING = "pending";
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String CANCELLED = "cancelled";


    private CarTransferResponseDTO toDto(CarTransferRequest r) {
        CarTransferResponseDTO dto = new CarTransferResponseDTO();
        dto.setId(r.getId());
        dto.setStatus(r.getStatus());
        dto.setCarId(r.getCar() != null ? r.getCar().getId() : null);
        dto.setFromUserId(r.getFromUser() != null ? r.getFromUser().getId() : null);
        dto.setToUserId(r.getToUser() != null ? r.getToUser().getId() : null);
        return dto;
    }

    public CarTransferResponseDTO directTransfer(Integer carId, Integer fromUserId, String toEmail, String toPhone) {
        if (carId == null || fromUserId == null || toEmail == null || toPhone == null) {
            throw new ApiException("carId و fromUserId و toEmail و toPhone مطلوبة");
        }

        Car car = carRepository.findCarById(carId);
        if (car == null) throw new ApiException("السيارة غير موجودة");

        User fromUser = userRepository.findUserById(fromUserId);
        if (fromUser == null) throw new ApiException("المستخدم (المرسل) غير موجود");

        // نجيب المستقبل من الإيميل + الجوال
        User toUser = userRepository.findUserByEmailIgnoreCaseAndPhone(toEmail.trim(), toPhone.trim());
        if (toUser == null) throw new ApiException("بيانات المستقبل غير صحيحة (الإيميل أو الجوال)");

        if (fromUser.getId().equals(toUser.getId()))
            throw new ApiException("لا يمكن إنشاء طلب نقل لنفس المستخدم");

        // تأكيد أن السيارة مملوكة للمرسل الآن
        if (car.getUser() == null || !car.getUser().getId().equals(fromUser.getId()))
            throw new ApiException("لا يمكنك إنشاء طلب نقل لسيارة لا تملكها");

        // (اختياري) منع تكرار طلب pending لنفس (car, from, to)
        boolean existsPending = transferRepo.findAllByFromUser_Id(fromUserId)
                .stream()
                .anyMatch(t ->
                        t.getCar() != null && t.getCar().getId().equals(carId) &&
                                t.getToUser() != null && t.getToUser().getId().equals(toUser.getId()) &&
                                PENDING.equals(t.getStatus())
                );
        if (existsPending) {
            throw new ApiException("يوجد طلب نقل مُعلّق لنفس السيارة بين نفس المستخدمين");
        }

        // ✅ إنشـاء طلب نقل بحالة pending (بدون نقل ملكية)
        CarTransferRequest req = new CarTransferRequest();
        req.setCar(car);
        req.setFromUser(fromUser);
        req.setToUser(toUser);
        req.setStatus(PENDING);

        return toDto(transferRepo.save(req));
    }


    public CarTransferResponseDTO accept(Integer requestId, Integer actingUserId){
        CarTransferRequest r = transferRepo.findCarTransferRequestById(requestId);
        if (r == null){
            throw new ApiException("طلب النقل غير موجود");
        }
        if (!PENDING.equals(r.getStatus())){
            throw new ApiException("لا يمكن قبول طلب غير مُعلّق");
        }

        if (transferRepo.findByIdAndToUser_Id(requestId,actingUserId) == null){
            throw new ApiException("فقط المستلم يستطيع قبول الطلب");
        }

        Car car = r.getCar();
        if (car.getUser() == null || !car.getUser().getId().equals(r.getFromUser().getId())){
            throw new ApiException("ملكية السيارة تغيّرت قبل القبول");
        }

        car.setUser(r.getToUser());
        carRepository.save(car);

        r.setStatus(ACCEPTED);
        transferRepo.save(r);

        return toDto(r);
    }

    public CarTransferResponseDTO cancel(Integer requestId , Integer actingUserId){
        CarTransferRequest r = transferRepo.findCarTransferRequestById(requestId);
        if (r == null){
            throw new ApiException("طلب النقل غير موجود");
        }

        if (!PENDING.equals(r.getStatus())){
            throw new ApiException("لا يمكن إلغاء طلب غير مُعلّق");
        }

        if (transferRepo.findByIdAndFromUser_Id(requestId, actingUserId) == null) {
            throw new ApiException("فقط منشئ الطلب يستطيع إلغاء الطلب");
        }

        r.setStatus(CANCELLED);
        transferRepo.save(r);

        return toDto(r);
    }

    public CarTransferResponseDTO reject(Integer requestId, Integer actingUserId) {
        CarTransferRequest r = transferRepo.findCarTransferRequestById(requestId);
        if (r == null) throw new ApiException("طلب النقل غير موجود");

        if (!PENDING.equals(r.getStatus()))
            throw new ApiException("لا يمكن رفض طلب غير مُعلّق");

        if (transferRepo.findByIdAndToUser_Id(requestId, actingUserId) == null)
            throw new ApiException("فقط المستلم يستطيع رفض الطلب");

        r.setStatus(REJECTED);
        transferRepo.save(r);
        return toDto(r);
    }

    public CarTransferResponseDTO getOne(Integer id) {
        CarTransferRequest r = transferRepo.findCarTransferRequestById(id);
        if (r == null) {
            throw new ApiException("طلب النقل غير موجود");
        }
        return toDto(r);
    }

    public List<CarTransferResponseDTO> getIncoming(Integer toUserId){
        return transferRepo.findAllByToUser_Id(toUserId).stream().map(this::toDto).toList();
    }

    public List<CarTransferResponseDTO> getOutgoing(Integer fromUserId) {
        return transferRepo.findAllByFromUser_Id(fromUserId).stream().map(this::toDto).toList();
    }

    public List<CarTransferResponseDTO> getByCar(Integer carId) {
        return transferRepo.findAllByCar_Id(carId).stream().map(this::toDto).toList();
    }

    public List<CarTransferResponseDTO> getByStatus(String status) {
        return transferRepo.findAllByStatus(status).stream().map(this::toDto).toList();
    }

}

