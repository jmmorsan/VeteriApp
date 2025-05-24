package veteriapp;

/**
 * Clase Dueno
 */
class Dueno {
    private String dniDueno;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;
    private String direccion;

    public Dueno(String dniDueno, String nombre, String apellidos, String telefono, String email, String direccion) {
        this.dniDueno = dniDueno;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }
    
    

    public String getDniDueno() {
		return dniDueno;
	}



	public void setDniDueno(String dniDueno) {
		this.dniDueno = dniDueno;
	}



	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public String getApellidos() {
		return apellidos;
	}



	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}



	public String getTelefono() {
		return telefono;
	}



	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getDireccion() {
		return direccion;
	}



	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}



	@Override
    public String toString() {
        return nombre + " " + apellidos + " (" + dniDueno + ")";
    }
}