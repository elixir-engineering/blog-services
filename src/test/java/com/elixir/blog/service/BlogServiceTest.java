package com.elixir.blog.service;

import com.elixir.blog.events.blogs.UpdateBlogEvent;
import com.elixir.blog.model.Blog;
import com.elixir.blog.repository.BlogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class BlogServiceTest {

    @Mock
    BlogRepository mockBlogRepository;

    @InjectMocks
    BlogService subject;

    @Test
    void addNewBlog() {
        final String title = "Hello World";
        final String content = "# Hello World";
        final UUID uuid = UUID.randomUUID();

        final Blog ret = Blog.builder().title(title).content(content).id(uuid).build();

        when(mockBlogRepository.save(any(Blog.class))).thenReturn(ret);

        Blog expected = subject.addNewBlog(title, content);
        assertAll(
                () -> assertNotNull(expected),
                () -> assertEquals(expected.getMarkdownContent(), ret.getMarkdownContent()),
                () -> assertEquals(expected.getTitle(), ret.getTitle())
        );
    }

    @Test
    void findBlog() {
        final String title = "Hello World";
        final String content = "# Hello World";
        final UUID uuid = UUID.randomUUID();
        final Blog ret = Blog.builder().title(title).content(content).id(uuid).build();

        final Optional<Blog> opt = Optional.of(ret);

        when(mockBlogRepository.findById(any())).thenReturn(opt);

        Optional<Blog> expected = subject.findBlog(uuid);

        assertAll(
                () -> assertTrue(expected.isPresent()),
                () -> assertNotNull(expected.get()),
                () -> assertEquals(expected.get(), ret),
                () -> assertEquals(expected.get().getMarkdownContent(), ret.getMarkdownContent()),
                () -> assertEquals(expected.get().getTitle(), ret.getTitle())
        );
    }

    @Test
    void updateBlog() {
        final String title = "Hello World";
        final String content = "# Hello World";
        final String updatedTitle = "First Blog";
        final Blog blog = Blog.builder().id(UUID.randomUUID()).content(content).title(title).build();


        when(this.mockBlogRepository.findById(blog.getId())).thenReturn(Optional.of(blog));
        when(this.mockBlogRepository.save(any(Blog.class)))
                .thenReturn(
                        Blog.builder().id(blog.getId())
                                .content(blog.getMarkdownContent())
                                .title(updatedTitle).build()
                );

        UpdateBlogEvent updateBlogEvent = UpdateBlogEvent.builder().title(updatedTitle).build();

        Blog updatedBlog = this.subject.updateBlog(blog.getId(), updateBlogEvent);

        assertAll(
                () -> assertNotNull(updatedBlog),
                () -> assertEquals(updatedBlog.getTitle(), updatedTitle),
                () -> assertEquals(updatedBlog.getMarkdownContent(), blog.getMarkdownContent()),
                () -> assertEquals(updatedBlog.getId(), blog.getId())
        );
    }
}