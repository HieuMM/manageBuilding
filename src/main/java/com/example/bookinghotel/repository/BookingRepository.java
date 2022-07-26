package com.example.bookinghotel.repository;

import com.example.bookinghotel.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

}

