package pallet_spring.model;

import lombok.Data;

import java.util.Date;

@Data
public class Post {

    private String id;
    private String content;
    private String pst_photo;
    private Date create_date;
    private Date update_date;
    private Date delete_date;
    private boolean share_check;


}
