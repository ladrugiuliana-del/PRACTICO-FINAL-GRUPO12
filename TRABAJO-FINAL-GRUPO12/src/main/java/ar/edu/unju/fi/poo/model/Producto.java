package ar.edu.unju.fi.poo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")


public class Producto {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String marca;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private double precioBase;
    
    public Producto(Integer id, String nombre, String descripcion, String marca, Categoria categoria, Integer stock,
			double precioBase) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.marca = marca;
		this.categoria = categoria;
		this.stock = stock;
		this.precioBase = precioBase;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public double getPrecioBase() {
		return precioBase;
	}

	public void setPrecioBase(double precioBase) {
		this.precioBase = precioBase;
	}

	public boolean verificarStock(int cantidadRequerida) {
        return this.stock >= cantidadRequerida;
    }

	public Producto() {
	}
	
    public void actualizarStock(int cantidadVendida) {
        if (verificarStock(cantidadVendida)) {
            this.stock -= cantidadVendida;
        } else {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + this.nombre);
        }
    }
}
