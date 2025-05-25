/**
 * Clase que representa una mascota en la clínica veterinaria.
 * Se asocia con un dueño y puede tener múltiples citas o tratamientos.
 *
 * @author Juan Manuel
 * @version 1.0
 * @since 2025-05-24
 */

package veteriapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Importar para formatear la fecha en toString()

enum EstadoMascota { ACTIVA, FALLECIDA }

public class Mascota {

    // Declaración de variables

    private int idMascota;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private double peso;
    private EstadoMascota estado;
    private LocalDate fechaFallecimiento;
    private String dniDueno;
    private String notasMemorial; 

    /**
     * Constructor para nuevas mascotas (cuando aún no tienen id, fecha de fallecimiento ni notas memorial específicas al inicio)
     * Este constructor establecerá el estado ACTIVA y fechaFallecimiento en null por defecto.
     * @param nombre            Nombre de la mascota
     * @param especie           Especie (perro, gato, etc.)
     * @param raza              Raza de la mascota
     * @param fechaNacimiento   Fecha de nacimiento
     * @param peso              Peso en kilogramos
     * @param dniDueno          DNI del dueño de la mascota
     */
    public Mascota(String nombre, String especie, String raza,
                   LocalDate fechaNacimiento, double peso, String dniDueno) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.estado = EstadoMascota.ACTIVA; // Por defecto al crear una mascota
        this.fechaFallecimiento = null;      // Por defecto al crear una mascota
        this.dniDueno = dniDueno;
        this.notasMemorial = null; // Por defecto null al crear una mascota activa
    }


    /**
     * Constructor completo de Mascota, utilizado generalmente al recuperar datos de la base de datos.
     *
     * @param idMascota         Id de la mascota
     * @param nombre            Nombre de la mascota
     * @param especie           Especie (perro, gato, etc.)
     * @param raza              Raza de la mascota
     * @param fechaNacimiento   Fecha de nacimiento
     * @param peso              Peso en kilogramos
     * @param estado            Estado de la mascota (activa/fallecida)
     * @param fechaFallecimiento Fecha de fallecimiento si aplica
     * @param dniDueno          DNI del dueño de la mascota
     * @param notasMemorial     Notas o detalles emocionales sobre la mascota (NUEVO)
     */
    public Mascota(int idMascota, String nombre, String especie, String raza,
                   LocalDate fechaNacimiento, double peso, EstadoMascota estado,
                   LocalDate fechaFallecimiento, String dniDueno, String notasMemorial) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.estado = estado;
        this.fechaFallecimiento = fechaFallecimiento;
        this.dniDueno = dniDueno;
        this.notasMemorial = notasMemorial; // Asignar el nuevo atributo
    }

    // Getters y Setters para todos los atributos

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public EstadoMascota getEstado() {
        return estado;
    }

    public void setEstado(EstadoMascota estado) {
        this.estado = estado;
    }

    public LocalDate getFechaFallecimiento() {
        return fechaFallecimiento;
    }

    public void setFechaFallecimiento(LocalDate fechaFallecimiento) {
        this.fechaFallecimiento = fechaFallecimiento;
    }

    public String getDniDueno() {
        return dniDueno;
    }

    public void setDniDueno(String dniDueno) {
        this.dniDueno = dniDueno;
    }

    // Getter y Setter para notasMemorial
    public String getNotasMemorial() {
        return notasMemorial;
    }

    public void setNotasMemorial(String notasMemorial) {
        this.notasMemorial = notasMemorial;
    }


    @Override
    public String toString() {
        // Usar DateTimeFormatter para un formato consistente si lo deseas, o simplemente LocalDate.toString()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String fechaNacimientoStr = (fechaNacimiento != null) ? fechaNacimiento.format(formatter) : "N/A";
        String fechaFallecimientoStr = (fechaFallecimiento != null) ? fechaFallecimiento.format(formatter) : "N/A";
        String notasMemorialStr = (notasMemorial != null && !notasMemorial.isEmpty()) ? "\n  Notas Memorial: " + notasMemorial : ""; // Añadir solo si no es nulo o vacío

        return "ID: " + idMascota +
               ", Nombre: " + nombre +
               ", Especie: " + especie +
               ", Raza: " + raza +
               ", Nacimiento: " + fechaNacimientoStr +
               ", Peso: " + peso + " kg" +
               ", Estado: " + estado +
               (estado == EstadoMascota.FALLECIDA ? ", Fallecimiento: " + fechaFallecimientoStr : "") + // Mostrar fecha fallecimiento solo si el estado es fallecida
               ", Dueño DNI: " + dniDueno +
               notasMemorialStr; // Añadir notas memorial al final
    }
}