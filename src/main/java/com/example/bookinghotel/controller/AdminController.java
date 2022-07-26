package com.example.bookinghotel.controller;

import com.example.bookinghotel.entity.BookingEntity;
import com.example.bookinghotel.entity.RoomEntity;
import com.example.bookinghotel.entity.UserEntity;
import com.example.bookinghotel.repository.BookingRepository;
import com.example.bookinghotel.repository.RoomRepository;
import com.example.bookinghotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
@MultipartConfig
public class AdminController {
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Value("${config.upload_folder}")
    String UPLOAD_FOLDER;


    /*
     * @author: HieuMM
     * @since: 26-Jul-22 1:40 PM
     * @description-VN:  CRUD room
     * @description-EN:
     * @param:
     * */
    @GetMapping("/showFormAddRoom")
    public String showFormAddRoom() {
        return "addRoom";
    }

    @GetMapping("/loadRoom")
    public String loadRoom(Model model) {
        List<RoomEntity> roomList = roomRepository.findAll();
        model.addAttribute("roomList", roomList);
        return "listRoom";
    }


    @PostMapping("/addRoom")
    public String addRoom(@RequestParam("pic1") MultipartFile file1, RoomEntity roomEntity) {
        String relativeFilePath1 = null;
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year = localDate.getYear();
        int day = localDate.getDayOfMonth();
        String subFolder = day + "_" + year + "/";
        String fullUploadDir = UPLOAD_FOLDER + subFolder;
        File checkDir = new File(fullUploadDir);
        if (!checkDir.exists() || checkDir.isFile()) {
            checkDir.mkdir();

        }
        try {
            relativeFilePath1 = subFolder + Instant.now().getEpochSecond() + file1.getOriginalFilename();
            Files.write(Paths.get(UPLOAD_FOLDER + relativeFilePath1), file1.getBytes());
            roomEntity.setImg(relativeFilePath1);
        } catch (IOException e) {
            System.out.println("ko upload duoc");
            e.printStackTrace();
        }
        roomEntity.setStatus(false);
        roomRepository.save(roomEntity);
        return "redirect:/loadRoom";
    }

    @GetMapping("/deleteRoom")
    public String deleteRoom(@RequestParam("id") Long id) {
        roomRepository.deleteById(id);
        return "redirect:/loadRoom";
    }

    @GetMapping("/detail")
    public ModelAndView update(Model model, @RequestParam("id") Long id) {
        ModelAndView mav = new ModelAndView("detailRoom");
        RoomEntity roomEntity = roomRepository.findById(id).get();
        mav.addObject("roomEntity", roomEntity);
        return mav;
    }

    /*
     * @author: HieuMM
     * @since: 26-Jul-22 1:40 PM
     * @description-VN:  CRUD user
     * @description-EN:
     * @param:
     * */
    @GetMapping("/loadUser")
    public String loadUser(Model model) {
        List<UserEntity> userList = userRepository.findAll();
        model.addAttribute("userList", userList);
        return "listEmployeeAcc";
    }

    @GetMapping("/showFormAddUser")
    public String showFormAddUser() {
        return "addEmployeeAcc";
    }

    @PostMapping("/addUser")
    public String addUser(UserEntity userEntity) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setRole("employee");
        userEntity.setPassword(encodedPassword);
        userRepository.save(userEntity);
        return "redirect:/loadUser";
    }

    /*
     * @author: HieuMM
     * @since: 26-Jul-22 2:17 PM
     * @description-VN:  booking room for admin
     * @description-EN:
     * @param:
     * */
    @GetMapping("/bookingRoomList")
    public String bookingRoomList(Model model) {
        List<BookingEntity> bookingEntityList = bookingRepository.findAll();
        model.addAttribute("bookingList", bookingEntityList);
        return "listBooking";
    }

    @GetMapping("/bookingRoomListEm")
    public String bookingRoomListEm(Model model) {
        List<BookingEntity> bookingEntityList = bookingRepository.findAll();
        model.addAttribute("bookingList", bookingEntityList);
        return "listBooking";
    }


    @GetMapping("/getFormBookingRoom")
    public String bookingRoom(Model model) {
        /*UserEntity userEntity = (UserEntity) session.getAttribute("user");*/
        List<RoomEntity> roomEntityList = roomRepository.findAllByStatus(false);
        model.addAttribute("roomEntityList", roomEntityList);
        /*RoomEntity roomEntity=roomRepository.findById(bookingEntity.getRoomEntity().getId()).get();
        roomEntity.setStatus(true);
        model.addAttribute("bookingEntity", bookingEntity);

        bookingEntity.setUserId(userEntity);
        bookingEntity.setStatus(true);
        bookingRepository.save(bookingEntity);
*/
        return "addBooking";
    }

    @PostMapping("/bookingRoom")
    public String bookingRoom(BookingEntity bookingEntity, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        RoomEntity roomEntity = roomRepository.findById(bookingEntity.getRoomEntity().getId()).get();
        roomEntity.setStatus(true);
        roomRepository.save(roomEntity);
        bookingEntity.setUserId(userEntity);
        bookingEntity.setStatus(true);
        bookingRepository.save(bookingEntity);
        return "redirect:/bookingRoomList";
    }

    @GetMapping("/updateBooking")
    public String cancelBooking(@RequestParam("id") Long id) {
        BookingEntity booking = bookingRepository.findById(id).get();
        booking.setStatus(false);
        bookingRepository.save(booking);
        RoomEntity roomEntity = roomRepository.findById(booking.getRoomEntity().getId()).get();
        roomEntity.setStatus(false);
        roomRepository.save(roomEntity);
        return "redirect:/bookingRoomList";
    }








    /*
     * @author: HieuMM
     * @since: 26-Jul-22 2:18 PM
     * @description-VN:  Login-Logout
     * @description-EN:
     * @param:
     * */

    @GetMapping("/loadFormLogin")
    public String loadFormLogin() {
        return "login";
    }

    @GetMapping("/403")
    public String error403() {
        return "403";
    }
}
