package br.com.furafila.imageapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.furafila.imageapp.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
