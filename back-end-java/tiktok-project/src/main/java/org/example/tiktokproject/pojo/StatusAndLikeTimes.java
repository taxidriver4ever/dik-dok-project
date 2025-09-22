package org.example.tiktokproject.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusAndLikeTimes {
    private boolean status;
    private Integer likeCount;
}
