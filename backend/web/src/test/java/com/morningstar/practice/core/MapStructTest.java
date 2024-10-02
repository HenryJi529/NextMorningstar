package com.morningstar.practice.core;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import lombok.Data;

public class MapStructTest {
    @Test
    public void test() {
        Car car = new Car();
        car.setMake("Toyota");
        car.setSeatNum(5);

        CarDto carDto = CarMapper.INSTANCE.carToCarDto(car);
        assert carDto.getMake().equals(car.getMake());
        assert carDto.getSeatCount() == car.getSeatNum();
    }
}

@Data
class Car {
    private String make;
    private int seatNum;
}


@Data
class CarDto {
    private String make;
    private int seatCount;
}

@Mapper
interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    @Mapping(source = "seatNum", target = "seatCount")
    CarDto carToCarDto(Car car);
}
