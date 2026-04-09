package com.example.dawak;

public class Medicine {
    private int id;
    private String name;
    private String dose;
    private String time;
    private String notes;
    private String status;

    public Medicine(int id, String name, String dose, String time, String notes, String status) {
        this.id = id;
        this.name = name;
        this.dose = dose;
        this.time = time;
        this.notes = notes;
        this.status = status;
    }

    public Medicine(String name, String dose, String time, String notes, String status) {
        this.name = name;
        this.dose = dose;
        this.time = time;
        this.notes = notes;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
