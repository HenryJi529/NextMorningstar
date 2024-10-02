package com.morningstar.kill.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDailyModeStats {
    private LocalDate date;
    private String mode;
    private String ratio;
}
