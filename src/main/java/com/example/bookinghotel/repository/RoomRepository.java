package com.example.bookinghotel.repository;

import com.example.bookinghotel.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
List<RoomEntity> findAllByStatus(Boolean status);
}
