package edu.pucmm;

import edu.pucmm.exception.DuplicateEmployeeException;
import edu.pucmm.exception.EmployeeNotFoundException;
import edu.pucmm.exception.InvalidSalaryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author me@fredpena.dev
 * @created 02/06/2024  - 00:47
 */

public class EmployeeManagerTest {

    private EmployeeManager employeeManager;
    private Position juniorDeveloper;
    private Position seniorDeveloper;
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setUp() {
        employeeManager = new EmployeeManager();
        juniorDeveloper = new Position("1", "Junior Developer", 30000, 50000);
        seniorDeveloper = new Position("2", "Senior Developer", 60000, 90000);
        employee1 = new Employee("1", "John Doe", juniorDeveloper, 40000);
        employee2 = new Employee("2", "Jane Smith", seniorDeveloper, 70000);
        employeeManager.addEmployee(employee1);
    }

    @Test
    public void testAddEmployee() {
        // - Agregar employee2 al employeeManager
        employeeManager.addEmployee(employee2);
        // - Verificar que el número total de empleados ahora es 2.
        assertEquals(2, employeeManager.getEmployees().size(), "Should have 2 employees after adding employee2");
        // - Verificar que employee2 está en la lista de empleados.
        assertTrue(employeeManager.getEmployees().contains(employee2), "Employee list should contain employee2");
    }

    @Test
    public void testRemoveEmployee() {
        // Agregar employee2 al employeeManager
        employeeManager.addEmployee(employee2);

        // Eliminar employee1 del employeeManager
        employeeManager.removeEmployee(employee1);

        // Verificar que el número total de empleados ahora es 1
        assertEquals(1, employeeManager.getEmployees().size(), "Should have 1 employee after removing employee1");

        // Verificar que employee1 ya no está en la lista de empleados
        assertFalse(employeeManager.getEmployees().contains(employee1), "Employee list should not contain employee1");
    }

    @Test
    public void testCalculateTotalSalary() {
        // Agregar employee2 al employeeManager
        employeeManager.addEmployee(employee2);

        // Verificar que el salario total es la suma de los salarios de employee1 y employee2
        double expectedTotalSalary = employee1.getSalary() + employee2.getSalary();
        assertEquals(expectedTotalSalary, employeeManager.calculateTotalSalary(),
                    "Total salary should be the sum of all employee salaries");
    }

    @Test
    public void testUpdateEmployeeSalaryValid() {
        // Actualizar el salario de employee1 a 45000
        double newSalary = 45000;
        employeeManager.updateEmployeeSalary(employee1, newSalary);

        // Verificar que el salario de employee1 ahora es 45000
        assertEquals(newSalary, employee1.getSalary(), "Employee salary should be updated to 45000");
    }

    @Test
    public void testUpdateEmployeeSalaryInvalid() {
        // Intentar actualizar el salario de employee1 a 60000 (que está fuera del rango para Junior Developer)
        double invalidSalary = 60000;

        // Verificar que se lanza una InvalidSalaryException
        InvalidSalaryException exception = assertThrows(InvalidSalaryException.class, () -> {
            employeeManager.updateEmployeeSalary(employee1, invalidSalary);
        }, "Should throw InvalidSalaryException when salary exceeds position's max salary");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception.getMessage().contains("Salary is not within the range"),
                   "Exception message should mention salary range");

        // Verificar que el salario no se actualizó
        assertEquals(40000, employee1.getSalary(), "Employee salary should remain unchanged");
    }

    @Test
    public void testUpdateEmployeeSalaryEmployeeNotFound() {
        // Intentar actualizar el salario de employee2 (no agregado al manager) a 70000
        double newSalary = 70000;

        // Verificar que se lanza una EmployeeNotFoundException
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeManager.updateEmployeeSalary(employee2, newSalary);
        }, "Should throw EmployeeNotFoundException when employee is not in the manager");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception.getMessage().contains("Employee not found"),
                   "Exception message should mention that the employee was not found");
    }

    @Test
    public void testUpdateEmployeePositionValid() {
        // Agregar employee2 al employeeManager
        employeeManager.addEmployee(employee2);

        // Crear una nueva posición para actualizar
        Position techLead = new Position("3", "Tech Lead", 70000, 100000);

        // Actualizar la posición de employee2 a techLead
        employeeManager.updateEmployeePosition(employee2, techLead);

        // Verificar que la posición de employee2 ahora es techLead
        assertEquals(techLead, employee2.getPosition(), "Employee position should be updated to Tech Lead");
    }

    @Test
    public void testUpdateEmployeePositionInvalidDueToSalary() {
        // Intentar actualizar la posición de employee1 a seniorDeveloper
        // El salario de employee1 (40000) está por debajo del rango para Senior Developer (60000-90000)

        // Verificar que se lanza una InvalidSalaryException
        InvalidSalaryException exception = assertThrows(InvalidSalaryException.class, () -> {
            employeeManager.updateEmployeePosition(employee1, seniorDeveloper);
        }, "Should throw InvalidSalaryException when employee's salary is below the minimum for the new position");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception.getMessage().contains("not within the range"),
                   "Exception message should mention salary range issues");

        // Verificar que la posición del empleado no cambió
        assertEquals(juniorDeveloper, employee1.getPosition(), "Employee position should remain unchanged");
    }

    @Test
    public void testUpdateEmployeePositionEmployeeNotFound() {
        // Intentar actualizar la posición de employee2 (no agregado al manager) a juniorDeveloper

        // Verificar que se lanza una EmployeeNotFoundException
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeManager.updateEmployeePosition(employee2, juniorDeveloper);
        }, "Should throw EmployeeNotFoundException when employee is not in the manager");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception.getMessage().contains("Employee not found"),
                   "Exception message should mention that the employee was not found");
    }

    @Test
    public void testIsSalaryValidForPosition() {
        // Verificar que un salario de 40000 es válido para juniorDeveloper
        assertTrue(employeeManager.isSalaryValidForPosition(juniorDeveloper, 40000),
                   "40000 should be valid for Junior Developer (range 30000-50000)");

        // Verificar que un salario de 60000 no es válido para juniorDeveloper
        assertFalse(employeeManager.isSalaryValidForPosition(juniorDeveloper, 60000),
                    "60000 should not be valid for Junior Developer (exceeds max 50000)");

        // Verificar que un salario de 70000 es válido para seniorDeveloper
        assertTrue(employeeManager.isSalaryValidForPosition(seniorDeveloper, 70000),
                   "70000 should be valid for Senior Developer (range 60000-90000)");

        // Verificar que un salario de 50000 no es válido para seniorDeveloper
        assertFalse(employeeManager.isSalaryValidForPosition(seniorDeveloper, 50000),
                    "50000 should not be valid for Senior Developer (below min 60000)");
    }

    @Test
    public void testAddEmployeeWithInvalidSalary() {
        // Crear un empleado con un salario de 60000 para juniorDeveloper (excede el máximo de 50000)
        Employee employeeWithHighSalary = new Employee("3", "Bob Johnson", juniorDeveloper, 60000);

        // Verificar que se lanza una InvalidSalaryException al agregar este empleado
        InvalidSalaryException exception1 = assertThrows(InvalidSalaryException.class, () -> {
            employeeManager.addEmployee(employeeWithHighSalary);
        }, "Should throw InvalidSalaryException when salary exceeds position's maximum");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception1.getMessage().contains("Invalid salary for position"),
                  "Exception message should mention invalid salary");

        // Crear otro empleado con un salario de 40000 para seniorDeveloper (por debajo del mínimo de 60000)
        Employee employeeWithLowSalary = new Employee("4", "Alice Brown", seniorDeveloper, 40000);

        // Verificar que se lanza una InvalidSalaryException al agregar este empleado
        InvalidSalaryException exception2 = assertThrows(InvalidSalaryException.class, () -> {
            employeeManager.addEmployee(employeeWithLowSalary);
        }, "Should throw InvalidSalaryException when salary is below position's minimum");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception2.getMessage().contains("Invalid salary for position"),
                  "Exception message should mention invalid salary");
    }

    @Test
    public void testRemoveExistentEmployee() {
        // Verificar que employee1 existe en el manager
        assertTrue(employeeManager.getEmployees().contains(employee1),
                   "Employee1 should be in the manager before removal");

        // Eliminar employee1 del employeeManager sin provocar excepciones
        assertDoesNotThrow(() -> {
            employeeManager.removeEmployee(employee1);
        }, "Removing an existing employee should not throw an exception");

        // Verificar que el empleado fue eliminado correctamente
        assertFalse(employeeManager.getEmployees().contains(employee1),
                    "Employee1 should not be in the manager after removal");

        // Verificar que la lista de empleados está vacía
        assertEquals(0, employeeManager.getEmployees().size(),
                    "Employee list should be empty after removing the only employee");
    }

    @Test
    public void testRemoveNonExistentEmployee() {
        // Verificar que employee2 no existe en el manager
        assertFalse(employeeManager.getEmployees().contains(employee2),
                    "Employee2 should not be in the manager before removal attempt");

        // Intentar eliminar employee2 (no agregado al manager)
        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeManager.removeEmployee(employee2);
        }, "Should throw EmployeeNotFoundException when removing a non-existent employee");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception.getMessage().contains("Employee not found"),
                   "Exception message should mention that the employee was not found");
    }

    @Test
    public void testAddDuplicateEmployee() {
        // Verificar que employee1 ya existe en el manager
        assertTrue(employeeManager.getEmployees().contains(employee1),
                   "Employee1 should already be in the manager before attempting to add duplicate");

        // Intentar agregar employee1 nuevamente al employeeManager
        DuplicateEmployeeException exception = assertThrows(DuplicateEmployeeException.class, () -> {
            employeeManager.addEmployee(employee1);
        }, "Should throw DuplicateEmployeeException when adding an employee with existing ID");

        // Verificar que el mensaje de la excepción es el esperado
        assertTrue(exception.getMessage().contains("Duplicate employee"),
                   "Exception message should mention duplicate employee");

        // Verificar que el número de empleados no cambió
        assertEquals(1, employeeManager.getEmployees().size(),
                    "Employee count should remain unchanged after duplicate add attempt");
    }

    @Test
    public void testIsSalaryValidForPositionWithNegativeSalary() {
        // Verificar que un salario negativo no es válido para cualquier posición
        assertFalse(employeeManager.isSalaryValidForPosition(juniorDeveloper, -5000),
                "Negative salary should not be valid for any position");
    }

    @Test
    public void testAddEmployeeSalaryBelowTenPercentMinimum() {
        // Crear un empleado con un salario por debajo del 10% del mínimo para juniorDeveloper
        double belowMinSalary = juniorDeveloper.getMinSalary() * 0.09; // 9% del mínimo
        Employee employeeWithTooLowSalary = new Employee("5", "John Low", juniorDeveloper, belowMinSalary);

        // Verificar que se lanza una InvalidSalaryException
        InvalidSalaryException exception = assertThrows(InvalidSalaryException.class, () -> {
            employeeManager.addEmployee(employeeWithTooLowSalary);
        }, "Should throw InvalidSalaryException when salary is below 10% of position's minimum");

        // Verificar el mensaje de la excepción
        assertTrue(exception.getMessage().contains("Invalid salary"),
                "Exception should mention invalid salary");
    }

    // BONUS
    // Si implementas al menos un @ParameterizedTest correctamente para validar distintos rangos salariales y posiciones, recibirás puntos adicionales.

    @ParameterizedTest
    @CsvSource({
            "30000, 1, true",   // Exactamente el mínimo para junior - válido
            "50000, 1, true",   // Exactamente el máximo para junior - válido
            "29999, 1, false",  // Justo por debajo del mínimo para junior - inválido
            "50001, 1, false",  // Justo por encima del máximo para junior - inválido
            "60000, 2, true",   // Exactamente el mínimo para senior - válido
            "90000, 2, true",   // Exactamente el máximo para senior - válido
            "59999, 2, false",  // Justo por debajo del mínimo para senior - inválido
            "90001, 2, false"   // Justo por encima del máximo para senior - inválido
    })

    public void testSalaryValidationWithParameters(double salary, String positionId, boolean expectedResult) {
        // Determinar qué posición usar basado en el ID
        Position position = "1".equals(positionId) ? juniorDeveloper : seniorDeveloper;

        // Verificar si el resultado de la validación coincide con lo esperado
        assertEquals(expectedResult, employeeManager.isSalaryValidForPosition(position, salary),
                "Salary " + salary + " validation for position " + position.getName() + " should be " + expectedResult);
    }
}
