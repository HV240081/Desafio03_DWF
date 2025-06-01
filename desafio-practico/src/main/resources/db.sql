CREATE TABLE Usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE Contenido (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('Pelicula', 'Serie')),
    descripcion VARCHAR(500),
    creadoPor BIGINT NOT NULL,
    FOREIGN KEY (creadoPor) REFERENCES Usuario(id)
);