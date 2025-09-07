package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.UserDTO;
import com.fkhrayef.motor.Model.Subscription;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // TODO: switch it to register and use registerDTO
    public void addUser(UserDTO userDTO) {
        User user = new User();
        // set DTO values
        user.setPhone(userDTO.getPhone());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setCity(userDTO.getCity());
        // set default values
        user.setRole("USER");

        userRepository.save(user);
    }

    public void uploadLicense(Integer userId, MultipartFile file, LocalDate licenseExpiry) {
        // Get user details from database
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found with id: " + userId);
        }

        // Validate file presence
        if (file == null || file.isEmpty()) {
            throw new ApiException("License file is required");
        }
        // Validate file type
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new ApiException("Only PDF files are allowed for license upload");
        }

        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException("License file size cannot exceed 10MB");
        }

        // Validate license expiry date
        if (licenseExpiry.isBefore(LocalDate.now())) {
            throw new ApiException("License expiry date must be in the future");
        }

        // Allow user to replace existing license (will overwrite in S3)
        // No need to check if license exists - we'll just overwrite it

        // Upload to S3 with unique naming
        String s3Url;
        try {
            s3Url = s3Service.uploadLicenseFile(file, userId.toString(), user.getPhone());
        } catch (Exception e) {
            throw new ApiException("Failed to upload license file: " + e.getMessage());
        }

        // Update user record with license information
        user.setLicenseFileUrl(s3Url);
        user.setLicenseExpiry(licenseExpiry);
        userRepository.save(user);
    }

    public byte[] downloadLicense(Integer userId) {
        // Get user details from database
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found with id: " + userId);
        }

        if (user.getLicenseFileUrl() == null) {
            throw new ApiException("No license file found for this user");
        }

        // Extract the S3 key from the URL
        String s3Url = user.getLicenseFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/licenses/") + 1); // Extract "licenses/user-123-phone-license.pdf"

        // Download file from S3
        return s3Service.downloadFile(key);
    }

    public void deleteLicense(Integer userId) {
        // Get user details from database
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found with id: " + userId);
        }

        if (user.getLicenseFileUrl() == null) {
            throw new ApiException("No license file found for this user");
        }

        // Extract the S3 key from the URL
        String s3Url = user.getLicenseFileUrl();
        String key = s3Url.substring(s3Url.indexOf("/licenses/") + 1); // Extract "licenses/user-123-phone-license.pdf"

        // Delete file from S3
        try {
            s3Service.deleteFile(key);
        } catch (Exception e) {
            throw new ApiException("Failed to delete license file from S3: " + e.getMessage());
        }

        // Clear license information from user record
        user.setLicenseFileUrl(null);
        user.setLicenseExpiry(null);
        userRepository.save(user);
    }

    public void updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }

        user.setPhone(userDTO.getPhone());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setCity(userDTO.getCity());

        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }

        userRepository.delete(user);
    }

    public String getUserSubscriptionType(Integer userId){
        User user = userRepository.findUserById(userId);
        if (user == null) throw new ApiException("User not found!");
        Subscription sub = user.getSubscription();
        if (sub == null) return "FREE";
        String p = sub.getPlanType();
        if (p == null) return "FREE";
        p = p.trim().toUpperCase();
        return ("PRO".equals(p) || "ENTERPRISE".equals(p)) ? p : "FREE";
    }
}
