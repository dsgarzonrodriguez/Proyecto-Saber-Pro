package saberPro.entities;

public class FiltrosConsulta {

    private Integer anio;
    private Integer semestre;
    private String programa;
    private String ciudad;
    private Integer idModulo;
    private String modulo;

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }

    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public Integer getIdModulo() { return idModulo; }
    public void setIdModulo(Integer idModulo) { this.idModulo = idModulo; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
}