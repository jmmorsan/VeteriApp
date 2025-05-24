package veteriapp;

import java.time.LocalDate;

/**
 * Enumeraciones de estado para mascota y cita
 */
enum EstadoMascota { ACTIVA, FALLECIDA }
enum EstadoCita { PENDIENTE, REALIZADA, CANCELADA }

/**
 * Clase Mascota
 */
class Mascota {
    private int idMascota;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private double peso;
    private EstadoMascota estado;
    private LocalDate fechaFallecimiento;
    private String dniDueno;

    public Mascota(int idMascota, String nombre, String especie, String raza, LocalDate fechaNacimiento, double peso, EstadoMascota estado, LocalDate fechaFallecimiento, String dniDueno) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.estado = estado;
        this.fechaFallecimiento = fechaFallecimiento;
        this.dniDueno = dniDueno;
    }



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



	@Override
    public String toString() {
        return nombre + " (" + especie + ", " + raza + ")";
    }
}