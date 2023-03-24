package com.gigajet.mhlb.domain.mypage.service;

import com.gigajet.mhlb.common.dto.SendMessageDto;
import com.gigajet.mhlb.common.util.S3Handler;
import com.gigajet.mhlb.common.util.SuccessCode;
import com.gigajet.mhlb.domain.mypage.dto.MypageRequestDto;
import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.user.entity.User;
import com.gigajet.mhlb.domain.user.repository.UserRepository;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.repository.WorkspaceRepository;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceInvite;
import com.gigajet.mhlb.domain.workspaceuser.entity.WorkspaceUser;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceInviteRepository;
import com.gigajet.mhlb.domain.workspaceuser.repository.WorkspaceUserRepository;
import com.gigajet.mhlb.exception.CustomException;
import com.gigajet.mhlb.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceInviteRepository workspaceInviteRepository;
    private final WorkspaceRepository workspaceRepository;

    private final S3Handler s3Handler;

    @Transactional(readOnly = true)
    public MypageResponseDto.Info userInfo(User user) {

        return MypageResponseDto.Info.builder()
                .user(user)
                .build();
    }

    @Transactional(readOnly = true)
    public MypageResponseDto.AllList workspaceInfo(User user) {
        List<MypageResponseDto.InviteList> inviteLists = new ArrayList<>();
        List<WorkspaceInvite> workspaceInviteList = workspaceInviteRepository.findByUser(user);

        List<MypageResponseDto.WorkspaceList> workspaceLists = new ArrayList<>();
        List<WorkspaceUser> workspaceUsers = workspaceUserRepository.findByUserAndIsShow(user, 1);

        for (WorkspaceInvite workspaceInvite: workspaceInviteList) {
            inviteLists.add(new MypageResponseDto.InviteList(workspaceInvite.getWorkspace()));
        }

        for (WorkspaceUser workspaceUser : workspaceUsers) {
            workspaceLists.add(new MypageResponseDto.WorkspaceList(workspaceUser.getWorkspace()));
        }
        return new MypageResponseDto.AllList(inviteLists, workspaceLists);
    }

    @Transactional(readOnly = true)
    public MypageResponseDto.Image showImage(User user) {
        return new MypageResponseDto.Image(user.getImage());
    }

    public MypageResponseDto.Image updateImage(User user, MultipartFile userImage) throws IOException {
        // 디폴트 이미지면 삭제 불가하게
        s3Handler.delete(user.getImage());
        String imageUrl = s3Handler.upload(userImage);
        userRepository.updateImage(imageUrl, user.getId());

        return new MypageResponseDto.Image(imageUrl);
    }

    public MypageResponseDto.Name updateName(User user, MypageRequestDto.Name nameRequest) {
        userRepository.updateUserName(nameRequest.getUserName(), user.getId());

        return new MypageResponseDto.Name(nameRequest.getUserName());
    }

    public MypageResponseDto.Description updateDesc(User user, MypageRequestDto.Description descRequest) {
        userRepository.updateDescription(descRequest.getUserDesc(), user.getId());

        return new MypageResponseDto.Description(descRequest.getUserDesc());
    }

    public MypageResponseDto.Job updateJob(User user, MypageRequestDto.Job jobRequest) {
        userRepository.updateJob(jobRequest.getUserJob(), user.getId());

        return new MypageResponseDto.Job(jobRequest.getUserJob());
    }

    @Transactional
    public ResponseEntity<SendMessageDto> deleteWorkspace(User user, long workspaceId) {
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserAndWorkspaceId(user, workspaceId).orElseThrow( ()-> new CustomException(ErrorCode.WRONG_WORKSPACE_ID));

        workspaceUser.updateIsShow();
//        workspaceUserRepository.deleteByUser_IdAndWorkspace_Id(user.getId(), workspaceId);

        return ResponseEntity.ok(SendMessageDto.of(SuccessCode.DELETE_SUCCESS));
    }

    @Transactional
    public ResponseEntity<SendMessageDto> inviteWorkspace(User user, long workspaceId) {
        Optional<WorkspaceInvite> workspaceUserOptional = workspaceInviteRepository.findByWorkspace_IdAndUserId(workspaceId, user.getId());

        if (workspaceUserOptional.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_WORKSPACE_ID);
        }
        Optional<Workspace> workspace= workspaceRepository.findById(workspaceId);

        Optional<WorkspaceUser> workspaceUser= Optional.of(new WorkspaceUser(user, workspace.get()));

        workspaceUserRepository.save(workspaceUser.get());

        workspaceInviteRepository.deleteByUser_IdAndWorkspace_Id(user.getId(), workspaceId);

        return ResponseEntity.ok(SendMessageDto.of(SuccessCode.INVITE_SUCCESS));
    }

    @Transactional
    public ResponseEntity<SendMessageDto> rejectWorkspace(User user, long workspaceId) {

        Optional<WorkspaceInvite> workspaceUserOptional = workspaceInviteRepository.findByWorkspace_IdAndUserId(workspaceId, user.getId());

        if (workspaceUserOptional.isEmpty()) {
            throw new CustomException(ErrorCode.WRONG_WORKSPACE_ID);
        }

        workspaceInviteRepository.deleteByUser_IdAndWorkspace_Id(user.getId(), workspaceId);

        return ResponseEntity.ok(SendMessageDto.of(SuccessCode.DELETE_SUCCESS));
    }
}
