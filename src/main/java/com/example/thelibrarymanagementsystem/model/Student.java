package com.example.thelibrarymanagementsystem.model;

public class Student {
    private String studentId;
    private String name;
    private InstitutionType institutionType;
    private String academicGroup;
    private String academicLevel;
    private String institutionName;
    private String address;
    private String phone;
    private String email;

    public Student(String studentId, String name, InstitutionType institutionType, String academicGroup,
                   String academicLevel, String institutionName, String address, String phone, String email) {
        this.studentId = studentId;
        this.name = name;
        this.institutionType = institutionType;
        this.academicGroup = academicGroup;
        this.academicLevel = academicLevel;
        this.institutionName = institutionName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public Student(String studentId, String name, String academicGroup, String institutionType,
                   String address, String phone, String email) {
        this(studentId, name, InstitutionType.fromLabel(institutionType), academicGroup, "", institutionType + " Campus", address, phone, email);
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public InstitutionType getInstitutionType() { return institutionType; }
    public void setInstitutionType(InstitutionType institutionType) { this.institutionType = institutionType; }
    public String getInstitute() { return institutionType.getLabel(); }
    public void setInstitute(String institute) { this.institutionType = InstitutionType.fromLabel(institute); }
    public String getAcademicGroup() { return academicGroup; }
    public void setAcademicGroup(String academicGroup) { this.academicGroup = academicGroup; }
    public String getAcademicLevel() { return academicLevel; }
    public void setAcademicLevel(String academicLevel) { this.academicLevel = academicLevel; }
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    public String getDepartment() { return academicGroup; }
    public void setDepartment(String department) { this.academicGroup = department; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStudentClass() { return institutionType == InstitutionType.SCHOOL ? academicGroup : ""; }
    public String getSection() { return institutionType == InstitutionType.SCHOOL ? academicLevel : ""; }
    public String getSchoolName() { return institutionType == InstitutionType.SCHOOL ? institutionName : ""; }
    public String getDepartmentProgram() { return institutionType != InstitutionType.SCHOOL ? academicGroup : ""; }
    public String getSemesterYear() { return institutionType != InstitutionType.SCHOOL ? academicLevel : ""; }
    public String getCollegeName() { return institutionType == InstitutionType.COLLEGE ? institutionName : ""; }
    public String getUniversityName() { return institutionType == InstitutionType.UNIVERSITY ? institutionName : ""; }

    @Override
    public String toString() {
        return studentId + " - " + name;
    }
}
