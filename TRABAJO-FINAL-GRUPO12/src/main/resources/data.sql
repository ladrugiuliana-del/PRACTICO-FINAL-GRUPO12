-- 1. CATEGORÍAS 
INSERT IGNORE INTO categorias (id, nombre) VALUES (1, 'Electrónica');
INSERT INTO categorias (id, nombre) VALUES (2, 'Hogar');
INSERT INTO categorias (id, nombre) VALUES (3, 'Indumentaria');

-- 2. CLIENTES 
INSERT INTO clientes (id, telefono, email, direccion) VALUES ('20300000001', '388111111', 'carlos.perez@email.com', 'Calle Belgrano 100');
INSERT INTO clientes (id, telefono, email, direccion) VALUES ('27300000002', '388222222', 'maria.gomez@email.com', 'Calle Alvear 200');
INSERT INTO clientes (id, telefono, email, direccion) VALUES ('20300000003', '388333333', 'lucas.martinez@email.com', 'Av. Fascio 300');
INSERT INTO clientes (id, telefono, email, direccion) VALUES ('30700000004', '388444444', 'contacto@tecno-sa.com', 'Av. Exodo 400');
INSERT INTO clientes (id, telefono, email, direccion) VALUES ('30700000005', '388555555', 'ventas@comercialjujuy.com', 'Calle Balcarce 500');

-- Particulares y Empresas
INSERT INTO particulares (id, nombre, apellido, dni) VALUES ('20300000001', 'Carlos', 'Pérez', '30000001');
INSERT INTO particulares (id, nombre, apellido, dni) VALUES ('27300000002', 'María', 'Gómez', '30000002');
INSERT INTO particulares (id, nombre, apellido, dni) VALUES ('20300000003', 'Lucas', 'Martínez', '30000003');

INSERT INTO empresas (id, razon_social) VALUES ('30700000004', 'Tecno S.A.');
INSERT INTO empresas (id, razon_social) VALUES ('30700000005', 'Comercial Jujuy S.R.L.');

-- 3. PRODUCTOS
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (1, 'Notebook 15.6"', 'Core i5 8GB SSD 256GB', 'Lenovo', 1, 10, 450000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (2, 'Mouse Inalámbrico', 'Óptico USB ergonómico', 'Logitech', 1, 25, 15000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (3, 'Teclado Mecánico', 'RGB Switch Red en español', 'Redragon', 1, 15, 35000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (4, 'Monitor 24"', 'Full HD IPS 75Hz', 'Samsung', 1, 8, 180000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (5, 'Auriculares Gamer', 'Con micrófono cancelación de ruido', 'HyperX', 1, 12, 50000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (6, 'Cafetera Expreso', '15 Bares de presión', 'Philips', 2, 5, 220000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (7, 'Pava Eléctrica', '1.7 Litros de acero inoxidable', 'Atma', 2, 20, 25000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (8, 'Tostadora', '2 Ranuras niveles de tostado', 'Moulinex', 2, 10, 30000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (9, 'Remera Básica', 'Algodón 100% talle L', 'Keating', 3, 30, 12000.0);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, precio_base) VALUES (10, 'Jean Clásico', 'Corte recto talle 42', 'Levi''s', 3, 18, 45000.0);

-- 4. CUPONES
INSERT INTO cupones (id, codigo, descuento, vencimiento) VALUES (1, 'DESC10', 10, '2026-12-31');
INSERT INTO cupones (id, codigo, descuento, vencimiento) VALUES (2, 'VERANO20', 20, '2026-12-31');

-- 5. VENTAS
-- Venta 1 (Contado con Cupón 1)
INSERT INTO ventas (id, codigo_comprobante, fecha, subtotal, total, cliente_id) VALUES (1, 'C-001', '2026-11-01', 465000.0, 418500.0, '20300000001');
INSERT INTO contado (id, cupon_id) VALUES (1, 1);

-- Venta 2 (Contado sin Cupón)
INSERT INTO ventas (id, codigo_comprobante, fecha, subtotal, total, cliente_id) VALUES (2, 'C-002', '2026-11-02', 35000.0, 35000.0, '27300000002');
INSERT INTO contado (id, cupon_id) VALUES (2, NULL);

-- Venta 3 (Financiada con Recargo)
INSERT INTO ventas (id, codigo_comprobante, fecha, subtotal, total, cliente_id) VALUES (3, 'F-001', '2026-11-03', 220000.0, 242000.0, '30700000004');
INSERT INTO financiada (id, recargo) VALUES (3, 10);

-- Venta 4 (Contado con Cupón 2)
INSERT INTO ventas (id, codigo_comprobante, fecha, subtotal, total, cliente_id) VALUES (4, 'C-003', '2026-11-04', 70000.0, 56000.0, '20300000003');
INSERT INTO contado (id, cupon_id) VALUES (4, 2);

-- Venta 5 (Financiada con Recargo)
INSERT INTO ventas (id, codigo_comprobante, fecha, subtotal, total, cliente_id) VALUES (5, 'F-002', '2026-11-05', 180000.0, 198000.0, '30700000005');
INSERT INTO financiada (id, recargo) VALUES (5, 10);

-- 6. DETALLES
-- Detalle Venta 1
INSERT INTO detalles (id, cantidad, precio_base, subtotal, venta_id, producto_id) VALUES (1, 1, 450000.0, 450000.0, 1, 1);
INSERT INTO detalles (id, cantidad, precio_base, subtotal, venta_id, producto_id) VALUES (2, 1, 15000.0, 15000.0, 1, 2);

-- Detalle Venta 2
INSERT INTO detalles (id, cantidad, precio_base, subtotal, venta_id, producto_id) VALUES (3, 1, 35000.0, 35000.0, 2, 3);

-- Detalle Venta 3
INSERT INTO detalles (id, cantidad, precio_base, subtotal, venta_id, producto_id) VALUES (4, 1, 220000.0, 220000.0, 3, 6);

-- Detalle Venta 4
INSERT INTO detalles (id, cantidad, precio_base, subtotal, venta_id, producto_id) VALUES (5, 1, 25000.0, 25000.0, 4, 7);
INSERT INTO detalles (id, cantidad, precio_base, subtotal, venta_id, producto_id) VALUES (6, 1, 45000.0, 45000.0, 4, 10);

-- Detalle Venta 5
INSERT INTO detalles (id, cantidad, precio_base, subtotal, venta_id, producto_id) VALUES (7, 1, 180000.0, 180000.0, 5, 4);

-- 7. CUOTAS (6 cuotas por cada Venta Financiada: Venta 3 y Venta 5)
-- Cuotas para Venta Financiada 3 (Total: 242000 / 6 = 40333.33)
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (1, 1, 40333.33, '2026-12-03', 'PAGADA', 3);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (2, 2, 40333.33, '2027-01-03', 'ADEUDADA', 3);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (3, 3, 40333.33, '2027-02-03', 'ADEUDADA', 3);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (4, 4, 40333.33, '2027-03-03', 'ADEUDADA', 3);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (5, 5, 40333.33, '2027-04-03', 'ADEUDADA', 3);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (6, 6, 40333.33, '2027-05-03', 'ADEUDADA', 3);

-- Cuotas para Venta Financiada 5 (Total: 198000 / 6 = 33000.00)
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (7, 1, 33000.00, '2026-12-05', 'PAGADA', 5);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (8, 2, 33000.00, '2027-01-05', 'ADEUDADA', 5);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (9, 3, 33000.00, '2027-02-05', 'ADEUDADA', 5);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (10, 4, 33000.00, '2027-03-05', 'ADEUDADA', 5);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (11, 5, 33000.00, '2027-04-05', 'ADEUDADA', 5);
INSERT INTO cuotas (id, n_cuota, importe, fecha_vencimiento, estado_cuota, venta_id) VALUES (12, 6, 33000.00, '2027-05-05', 'ADEUDADA', 5);