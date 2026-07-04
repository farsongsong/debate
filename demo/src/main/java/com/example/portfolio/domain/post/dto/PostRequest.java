package com.example.portfolio.domain.post.dto;
import lombok.Getter; import lombok.Setter;
import java.util.List;
@Getter @Setter
public class PostRequest { private String title, content, summary; private List<String> tags; }
