package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.CommentRepository;
import traintickets.businesslogic.session.SessionManager;
import traintickets.businesslogic.transport.UserInfo;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void addComment_positive_saved() {
        var comment = new Comment(null, new UserId(1), new TrainId(737), 5, "good");
        var role = "user_role";
        var userInfo = new UserInfo(new UserId(1L), role);
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        commentService.addComment(sessionId, comment);
        verify(commentRepository).addComment(role, comment);
    }

    @Test
    void addComment_negative_invalid() {
        var comment = new Comment(null, new UserId(1), new TrainId(737), 6, "good");
        assertThrows(InvalidEntityException.class, () -> commentService.addComment(UUID.randomUUID(), comment));
        verify(commentRepository, never()).addComment(any(), any());
    }

    @Test
    void addComment_negative_userIdMismatched() {
        var comment = new Comment(null, new UserId(1), new TrainId(737), 5, "good");
        var role = "user_role";
        var userInfo = new UserInfo(new UserId(2), role);
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        assertThrows(InvalidEntityException.class, () -> commentService.addComment(sessionId, comment));
        verify(commentRepository, never()).addComment(any(), any());
    }

    @Test
    void getComments_positive_got() {
        var trainId = new TrainId(228);
        var comm1 = new Comment(new CommentId(1), new UserId(1), trainId, 5, "good");
        var comm2 = new Comment(new CommentId(2), new UserId(2), trainId, 1, "bad");
        var role = "user_role";
        var userInfo = new UserInfo(null, role);
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        given(commentRepository.getComments(role, trainId)).willReturn(List.of(comm1, comm2));
        var comments = commentService.getComments(sessionId, trainId);
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals(comm1, comments.get(0));
        assertEquals(comm2, comments.get(1));
    }

    @Test
    void getComments_positive_empty() {
        var trainId = new TrainId(228);
        var role = "user_role";
        var userInfo = new UserInfo(null, role);
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        given(commentRepository.getComments(role, trainId)).willReturn(List.of());
        var result = commentService.getComments(sessionId, trainId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteComment_positive_deleted() {
        var commId = new CommentId(1);
        var role = "user_role";
        var userInfo = new UserInfo(null, role);
        var sessionId = UUID.randomUUID();
        given(sessionManager.getUserInfo(sessionId)).willReturn(userInfo);
        commentService.deleteComment(sessionId, commId);
        verify(commentRepository).deleteComment(role, commId);
    }
}