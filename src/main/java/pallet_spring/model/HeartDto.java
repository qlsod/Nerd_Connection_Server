package pallet_spring.model;

import lombok.Data;

@Data
public class HeartDto {

//    private int heart_no;
    private int user_no;
    private int post_no;

    public HeartDto(int user_no, int post_no) {
        this.user_no = user_no;
        this.post_no = post_no;
    }
}
