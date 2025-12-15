IF NOT EXISTS (
    SELECT 1 FROM sys.tables WHERE name = 'users'
)
BEGIN
CREATE TABLE users (
               id INT IDENTITY(1,1) PRIMARY KEY,
               username NVARCHAR(100) NOT NULL,
               created_at DATETIME2 DEFAULT SYSDATETIME()
);
END
GO