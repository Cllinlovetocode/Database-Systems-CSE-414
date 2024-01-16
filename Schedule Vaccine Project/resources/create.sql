CREATE TABLE Caregivers (
    Username varchar(255),
    Salt BINARY(16),
    Hash BINARY(16),
    PRIMARY KEY (Username)
);

CREATE TABLE Availabilities (
    Time DATE,
    Username varchar(255) REFERENCES Caregivers,
    PRIMARY KEY (Time, Username)
);

CREATE TABLE Vaccines (
    Name varchar(255) PRIMARY KEY,
    Doses INT

);

CREATE TABLE Patients (
    PatientUsername varchar(255) PRIMARY KEY,
    Salt BINARY(16),
    Hash BINARY(16)

);

CREATE TABLE Appointment (
    AppointmentID BIGINT PRIMARY KEY,
    date DATE,
    PatientUsername VARCHAR(255) REFERENCES Patients,
    Name VARCHAR(255) REFERENCES Vaccines,
    Username VARCHAR (255) REFERENCES Caregivers
);


