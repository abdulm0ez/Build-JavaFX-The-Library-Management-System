package com.example.thelibrarymanagementsystem.model;

import java.time.LocalDate;

public class SecurityDeposit {
    private String depositId;
    private String studentId;
    private double depositAmount;
    private LocalDate depositDate;
    private DepositStatus depositStatus;
    private RefundStatus refundStatus;

    public SecurityDeposit(String depositId, String studentId, double depositAmount, LocalDate depositDate,
                           DepositStatus depositStatus, RefundStatus refundStatus) {
        this.depositId = depositId;
        this.studentId = studentId;
        this.depositAmount = depositAmount;
        this.depositDate = depositDate;
        this.depositStatus = depositStatus;
        this.refundStatus = refundStatus;
    }

    public String getDepositId() { return depositId; }
    public String getStudentId() { return studentId; }
    public double getDepositAmount() { return depositAmount; }
    public LocalDate getDepositDate() { return depositDate; }
    public DepositStatus getDepositStatus() { return depositStatus; }
    public void setDepositStatus(DepositStatus depositStatus) { this.depositStatus = depositStatus; }
    public RefundStatus getRefundStatus() { return refundStatus; }
    public void setRefundStatus(RefundStatus refundStatus) { this.refundStatus = refundStatus; }

    public boolean isActiveForIssue() {
        return depositStatus == DepositStatus.VERIFIED && refundStatus == RefundStatus.NOT_REFUNDED;
    }
}
