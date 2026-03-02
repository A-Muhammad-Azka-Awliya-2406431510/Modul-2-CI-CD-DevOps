package id.ac.ui.cs.advprog.eshop.service;
import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CarServiceImpl implements CarService{
    @Autowired
    private CarRepositoryPort carRepository;

    @Override
    public Car create(Car car) {
        if (car == null) return null;
        return carRepository.create(car);
    }

    @Override
    public List<Car> findAll() {
        Iterator<Car> carIterator = carRepository.findAll();
        List<Car> allCar = new ArrayList<>();
        carIterator.forEachRemaining(allCar::add);
        return allCar;
    }

    @Override
    public Car findById(String carId) {
        Car car = carRepository.findById(carId);
        return car;
    }

    @Override
    public boolean update(Car car) {
        if (car == null || car.getCarId() == null || car.getCarId().isBlank()) return false;
        carRepository.update(car.getCarId(), car);
        return true;
    }

    @Override
    public boolean delete(String id) {
        if (id == null || id.isBlank()) return false;
        carRepository.delete(id);
        return true;
    }
}
