package ru.ifmo.web.database.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menagerie {
    private Long id;
    private String animal;
    private String name;
    private String breed;
    private String health;
    private Date arrival;
}
