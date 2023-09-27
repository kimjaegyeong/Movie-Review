package com.example.mreview.controller;

import com.example.mreview.dto.MovieDTO;
import com.example.mreview.dto.PageRequestDTO;
import com.example.mreview.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movie")
@Slf4j
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    @GetMapping("/register")
    public void register(){

    }
    @PostMapping("/register")
    public String register(MovieDTO movieDTO, RedirectAttributes redirectAttributes){
        log.info("movieDTO: "+movieDTO);
        Long mno = movieService.register(movieDTO);
        redirectAttributes.addFlashAttribute("msg",mno);
        return "redirect:/movie/list";

    }

    @GetMapping("list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        log.info("pageRequestDTO: "+ pageRequestDTO);
        model.addAttribute("result", movieService.getList(pageRequestDTO));
    }

    @GetMapping({"/read","/modify"})
    public void read(long mno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO , Model model){
        log.info(mno+": mon");
        MovieDTO movieDTO = movieService.getMovie(mno);
        model.addAttribute("dto", movieDTO);

    }
}
