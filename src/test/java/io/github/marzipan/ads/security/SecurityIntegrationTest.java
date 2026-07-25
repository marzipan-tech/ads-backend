package io.github.marzipan.ads.security;

import io.github.marzipan.ads.entity.Ad;
import io.github.marzipan.ads.entity.Comment;
import io.github.marzipan.ads.entity.Role;
import io.github.marzipan.ads.entity.User;
import io.github.marzipan.ads.repository.AdRepository;
import io.github.marzipan.ads.repository.CommentRepository;
import io.github.marzipan.ads.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private CommentRepository commentRepository;

    private static final String AD_JSON = "{"
            + "\"title\": \"title\","
            + "\"price\": 100,"
            + "\"description\": \"description\""
            + "}";
    private static final String COMMENT_JSON = "{"
            + "\"text\": \"some text\""
            + "}";

    @Test
    void getAllAds_shouldBeAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk());
    }

    @Test
    void getMyAds_shouldRequireAuthentification() throws Exception {
        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateAd_shouldBeUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(patch("/ads/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateAd_shouldBeAccessibleForOwner() throws Exception {
        User user = createUser();
        Ad ad = createAd(user);

        mockMvc.perform(patch("/ads/{id}", ad.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AD_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "anotherUser", roles = "USER")
    void updateAd_shouldBeForbiddenForNonOwner() throws Exception {
        User user = createUser();
        Ad ad = createAd(user);

        mockMvc.perform(patch("/ads/{id}", ad.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AD_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void updateAd_shouldBeAccessibleForAdmin() throws Exception {
        User user = createUser();
        Ad ad = createAd(user);

        mockMvc.perform(patch("/ads/{id}", ad.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AD_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void updateComment_shouldBeAccessibleForOwner() throws Exception {
        User user = createUser();
        Ad ad = createAd(user);
        Comment comment = createComment(user, ad);

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMMENT_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "anotherUser", roles = "USER")
    void updateComment_shouldBeForbiddenForNonOwner() throws Exception {
        User user = createUser();
        Ad ad = createAd(user);
        Comment comment = createComment(user, ad);

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", ad.getId(), comment.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMMENT_JSON))
                .andExpect(status().isForbidden());
    }

    private User createUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    private Ad createAd(User author) {
        Ad ad = new Ad();
        ad.setAuthor(author);
        ad.setTitle("title");
        ad.setPrice(100);
        ad.setDescription("description");
        return adRepository.save(ad);
    }

    private Comment createComment(User author, Ad ad) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setAd(ad);
        comment.setText("text");
        return commentRepository.save(comment);
    }
}
