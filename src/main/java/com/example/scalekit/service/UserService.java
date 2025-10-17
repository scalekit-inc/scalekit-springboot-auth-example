package com.example.scalekit.service;

import com.scalekit.ScalekitClient;
import com.scalekit.api.UserClient;
import com.scalekit.grpc.scalekit.v1.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private ScalekitClient scalekitClient;

    @Value("${scalekit.organization-id}")
    private String organizationId;

    private UserClient getUserClient() {
        return scalekitClient.users();
    }

    /**
     * List all users in the organization
     */
    public ListOrganizationUsersResponse listOrganizationUsers(Integer pageSize, String pageToken) {
        ListOrganizationUsersRequest request = ListOrganizationUsersRequest.newBuilder()
                .setOrganizationId(organizationId)
                .setPageSize(pageSize != null ? pageSize : 50)
                .setPageToken(pageToken != null ? pageToken : "")
                .build();
        
        return getUserClient().listOrganizationUsers(organizationId, request);
    }

    /**
     * Get user details by user ID
     */
    public GetUserResponse getUser(String userId) {
        return getUserClient().getUser(userId);
    }

    /**
     * Create a new user with membership in the organization
     */
    public CreateUserAndMembershipResponse createUserAndMembership(CreateUser createUser, boolean sendInvitationEmail) {
        CreateUserAndMembershipRequest request = CreateUserAndMembershipRequest.newBuilder()
                .setOrganizationId(organizationId)
                .setUser(createUser)
                .setSendInvitationEmail(sendInvitationEmail)
                .build();
        
        return getUserClient().createUserAndMembership(organizationId, request);
    }

    /**
     * Update user information
     */
    public UpdateUserResponse updateUser(String userId, UpdateUser updateUser) {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUser(updateUser)
                .build();
        
        return getUserClient().updateUser(userId, request);
    }

    /**
     * Delete a user
     */
    public void deleteUser(String userId) {
        getUserClient().deleteUser(userId);
    }

    /**
     * Create membership for a user in the organization
     */
    public CreateMembershipResponse createMembership(String userId, CreateMembership createMembership, boolean sendInvitationEmail) {
        CreateMembershipRequest request = CreateMembershipRequest.newBuilder()
                .setOrganizationId(organizationId)
                .setMembership(createMembership)
                .setSendInvitationEmail(sendInvitationEmail)
                .build();
        
        return getUserClient().createMembership(organizationId, userId, request);
    }

    /**
     * Update user membership in the organization
     */
    public UpdateMembershipResponse updateMembership(String userId, UpdateMembership updateMembership) {
        UpdateMembershipRequest request = UpdateMembershipRequest.newBuilder()
                .setOrganizationId(organizationId)
                .setMembership(updateMembership)
                .build();
        
        return getUserClient().updateMembership(organizationId, userId, request);
    }

    /**
     * Delete user membership from the organization
     */
    public void deleteMembership(String userId) {
        getUserClient().deleteMembership(organizationId, userId);
    }

    /**
     * Resend invitation email to a user
     */
    public ResendInviteResponse resendInvite(String userId) {
        return getUserClient().resendInvite(organizationId, userId);
    }
}
