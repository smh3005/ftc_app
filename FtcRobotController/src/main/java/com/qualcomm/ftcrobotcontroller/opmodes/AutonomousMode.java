package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by smh30 on 6/3/2016.
 */
public class AutonomousMode extends OpMode {

    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor shoulder;
    DcMotor elbow;
    Servo allClearFinger;
    Servo leftZipLineFlipper;
    Servo rightZipLineFlipper;

    ServoController servoC1;

    enum State {drivingFoward, turingAround};
    ElapsedTime time;

    double drivingForwardTime = 2.0;
    double turningAroundTime = 1.5;
    int count = 0;

    @Override
    public void init() {
        try {
            initializeAutonomousMode();
        } catch (Exception e) {
            DbgLog.error("Seth - Initialization Robot Core Exception Error");
        }
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        this.rightDrive.setPower(0);
        this.leftDrive.setPower(0);
        this.shoulder.setPower(0);
        this.elbow.setPower(0);
    }

    private void initializeAutonomousMode() {
        //get references to the hardware from the hardware map
        this.leftDrive = hardwareMap.dcMotor.get("leftDrive");
        this.rightDrive = hardwareMap.dcMotor.get("rightDrive");

        this.shoulder = hardwareMap.dcMotor.get("shoulder");
        this.elbow = hardwareMap.dcMotor.get("elbow");

        this.servoC1 = hardwareMap.servoController.get("servoC1");
        servoC1.pwmEnable();

        this.leftDrive.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.rightDrive.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.shoulder.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        this.elbow.setMode(DcMotorController.RunMode.RESET_ENCODERS);


    }


}
