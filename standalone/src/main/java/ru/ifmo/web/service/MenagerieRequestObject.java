package ru.ifmo.web.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@XmlRootElement
@AllArgsConstructor
@NoArgsConstructor
public class MenagerieRequestObject {
    private Long id;
    private String animal;
    private String name;
    private String breed;
    private String health;
    private Date arrival;
}
