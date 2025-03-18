package ru.traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.traintickets.businesslogic.model.CommId;
import ru.traintickets.businesslogic.model.Comment;
import ru.traintickets.businesslogic.model.TrainId;
import ru.traintickets.businesslogic.model.UserId;
import ru.traintickets.businesslogic.repository.CommentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    void addComment_positive_saved() {
        var comment = new Comment(null, new UserId("random_username"), new TrainId(737), 5, "good");
        commentService.addComment(comment);
        verify(commentRepository).addComment(comment);
    }

    @Test
    void getComments_positive_got() {
        var trainId = new TrainId(228);
        var comm1 = new Comment(new CommId(1), new UserId("random_username1"), trainId, 5, "good");
        var comm2 = new Comment(new CommId(2), new UserId("random_username2"), trainId, 1, "bad");
        given(commentRepository.getComments(trainId)).willReturn(List.of(comm1, comm2));
        var comments = commentService.getComments(trainId);
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertEquals(comm1, comments.get(0));
        assertEquals(comm2, comments.get(1));
    }

    @Test
    void getComments_positive_empty() {
        var trainId = new TrainId(228);
        given(commentRepository.getComments(trainId)).willReturn(List.of());
        var comments = commentService.getComments(trainId);
        assertNotNull(comments);
        assertEquals(0, comments.size());
    }

    @Test
    void deleteComment_positive_deleted() {
        var commId = new CommId(1);
        commentService.deleteComment(commId);
        verify(commentRepository).deleteComment(commId);
    }
}