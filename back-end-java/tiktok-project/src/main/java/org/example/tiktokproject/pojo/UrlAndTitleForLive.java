package org.example.tiktokproject.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlAndTitleForLive implements Serializable {
    private String url;
    private String title;
}
