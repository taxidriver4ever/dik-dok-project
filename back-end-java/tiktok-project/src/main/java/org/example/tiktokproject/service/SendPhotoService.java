package org.example.tiktokproject.service;

import org.example.tiktokproject.pojo.PhotoAndVideo;

public interface SendPhotoService {
    void SendToPython(PhotoAndVideo photoAndVideo);
}
