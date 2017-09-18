package cn.yan.dao;

import cn.yan.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDao extends JpaRepository<Task,Integer>{
}
