package cn.shu.dao;

import cn.shu.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmDao  extends JpaRepository<Film,String> {
}
