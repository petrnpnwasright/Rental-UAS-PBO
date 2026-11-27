package rental.model;

/**
 * Kelas abstrak dasar untuk seluruh pengguna sistem (Admin dan Penyewa).
 * Menyediakan mekanisme autentikasi login dan enkapsulasi kredensial akun.
 *
 * @version 1.0
 */
public abstract class Akun {

    private String id;
    private String username;
    private String password;
    private String namaLengkap;

    /**
     * Konstruktor dasar Akun pengguna.
     *
     * @param id          ID unik akun
     * @param username    nama pengguna untuk login
     * @param password    kata sandi akun
     * @param namaLengkap nama lengkap pengguna untuk tampilan profil
     */
    public Akun(String id, String username, String password, String namaLengkap) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
    }

    /**
     * Memverifikasi kredensial login terhadap username dan password akun ini.
     *
     * @param inputUsername username yang dimasukkan pengguna
     * @param inputPassword password yang dimasukkan pengguna
     * @return true jika kredensial cocok, false jika tidak cocok
     */
    public boolean login(String inputUsername, String inputPassword) {
        if (inputUsername == null || inputPassword == null) {
            return false;
        }
        return this.username.equalsIgnoreCase(inputUsername.trim())
                && this.password.equals(inputPassword);
    }

    /**
     * Method login tanpa parameter sesuai spesifikasi minimum Class Diagram.
     *
     * @return true apabila akun memiliki username dan password yang valid
     */
    public boolean login() {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }

    /**
     * Mengembalikan peran akun (ADMIN atau PENYEWA).
     *
     * @return string peran akun
     */
    public abstract String getRole();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }
}
