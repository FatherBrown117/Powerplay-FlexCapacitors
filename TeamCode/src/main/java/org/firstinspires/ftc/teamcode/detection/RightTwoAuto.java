
        /*
         * Copyright (c) 2021 OpenFTC Team
         *
         * Permission is hereby granted, free of charge, to any person obtaining a copy
         * of this software and associated documentation files (the "Software"), to deal
         * in the Software without restriction, including without limitation the rights
         * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
         * copies of the Software, and to permit persons to whom the Software is
         * furnished to do so, subject to the following conditions:
         *
         * The above copyright notice and this permission notice shall be included in all
         * copies or substantial portions of the Software.
         * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
         * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
         * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
         * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
         * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
         * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
         * SOFTWARE.
         */

        package org.firstinspires.ftc.teamcode.detection;

        import com.acmerobotics.roadrunner.geometry.Pose2d;
        import com.acmerobotics.roadrunner.trajectory.Trajectory;
        import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
        import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.DcMotorSimple;
        import com.qualcomm.robotcore.hardware.Servo;

        import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
        import org.firstinspires.ftc.teamcode.BasicAuto;
        import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
        import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
        import org.openftc.apriltag.AprilTagDetection;
        import org.openftc.easyopencv.OpenCvCamera;
        import org.openftc.easyopencv.OpenCvCameraFactory;
        import org.openftc.easyopencv.OpenCvCameraRotation;

        import java.util.ArrayList;

@Autonomous
public class RightTwoAuto extends LinearOpMode {


    BasicAuto obj = new BasicAuto();

    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor rightRear = null;
    private DcMotor leftRear = null;
    private DcMotor rightArm = null;
    private DcMotor leftArm = null;
    private Servo rightClaw = null;
    private Servo leftClaw = null;

    //Starting Posistion

    private final Pose2d home = new Pose2d(-42,68,90.0);

    // Roadrunner Trajectory Variables
    private TrajectorySequence turn90;
    private TrajectorySequence FaceStraight;
    private TrajectorySequence ForwardToHighJunction;
    //private TrajectorySequence ;





    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;

    //Tags 1, 2, and 3 from the AprilTags family 36h11
    int LEFT = 1;
    int MIDDLE = 2;
    int RIGHT = 3;

    AprilTagDetection tagOfInterest = null;

    @Override
    public void runOpMode()
    {



        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftRear = hardwareMap.get(DcMotor.class, "leftRear");
        rightRear = hardwareMap.get(DcMotor.class, "rightRear");
        rightArm = hardwareMap.get(DcMotor.class, "rightArm");
        leftArm = hardwareMap.get(DcMotor.class, "leftArm");

        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightRear.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightArm.setDirection(DcMotorSimple.Direction.REVERSE);

        rightClaw = hardwareMap.get(Servo.class, "rightClaw");
        leftClaw = hardwareMap.get(Servo.class, "leftClaw");

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);

        // RoadRunner Hardware Mapping and Trajectories //




        servoClose();
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPos = new Pose2d(33, 65, Math.toRadians(180)); // Breaks Code Edit: Not anymore

        Trajectory trajStart = drive.trajectoryBuilder(new Pose2d())
                .forward(4)
                .build();

        Trajectory trajLeftToFirstJunction = drive.trajectoryBuilder(trajStart.end())
                .strafeLeft(43)
                .build();

        Trajectory trajForwardToFirstJunction = drive.trajectoryBuilder(trajLeftToFirstJunction.end())
                .forward(26)
                .build();

        Trajectory trajSlightlyForwardToFirstJunction = drive.trajectoryBuilder(trajForwardToFirstJunction.end()) // 0 20 before
                .forward(6)
                .build();

        Trajectory trajSlightlyBackwardToFirstJunction = drive.trajectoryBuilder(trajSlightlyForwardToFirstJunction.end()) // 0 20 before
                .back(6)
                .build();

        Trajectory trajRightToConeStack = drive.trajectoryBuilder(trajSlightlyBackwardToFirstJunction.end())
                .strafeRight(43)
                .build();



        turn90 = drive.trajectorySequenceBuilder( trajRightToConeStack.end())
                .turn(Math.toRadians(-90))
                .build();

        Trajectory trajLeftToConeStack = drive.trajectoryBuilder(turn90.end())
                .strafeLeft(25)
                .build();

        Trajectory trajForwardToConeStack = drive.trajectoryBuilder(trajLeftToConeStack.end())
                .forward(27)
                .build();

        Trajectory trajAwayConeStack = drive.trajectoryBuilder(trajForwardToConeStack.end())
                .back(37)
                .build();

        FaceStraight = drive.trajectorySequenceBuilder(trajAwayConeStack.end())
                .turn(Math.toRadians(-90))
                .build();

        Trajectory trajSlightlyForwardToSecondJunction = drive.trajectoryBuilder(FaceStraight.end())
                .forward(6)
                .build();

        Trajectory trajSlightlyBackwardToSecondJunction = drive.trajectoryBuilder(trajSlightlyForwardToSecondJunction.end())
                .back(6)
                .build();

        // Three Parking Trajectories

        Trajectory trajZone1 = drive.trajectoryBuilder(trajSlightlyBackwardToSecondJunction.end())
                .strafeRight(13)
                .build();

        Trajectory trajZone2 = drive.trajectoryBuilder(trajSlightlyBackwardToSecondJunction.end())
                .strafeLeft(12)
                .build();

        Trajectory trajZone3 = drive.trajectoryBuilder(trajSlightlyBackwardToSecondJunction.end())
                .strafeLeft(39)
                .build();


        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

        telemetry.setMsTransmissionInterval(50);

        /*
         * The INIT-loop:
         * This REPLACES waitForStart!
         */
        while (!isStarted() && !isStopRequested())
        {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            if(currentDetections.size() != 0)
            {
                boolean tagFound = false;

                for(AprilTagDetection tag : currentDetections)
                {
                    if(tag.id == LEFT || tag.id == MIDDLE || tag.id == RIGHT)
                    {
                        tagOfInterest = tag;
                        tagFound = true;
                        break;
                    }
                }

                if(tagFound)
                {
                    telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                    tagToTelemetry(tagOfInterest);
                }
                else
                {
                    telemetry.addLine("Don't see tag of interest :(");

                    if(tagOfInterest == null)
                    {
                        telemetry.addLine("(The tag has never been seen)");
                    }
                    else
                    {
                        telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                        tagToTelemetry(tagOfInterest);
                    }
                }

            }
            else
            {
                telemetry.addLine("Don't see tag of interest :(");

                if(tagOfInterest == null)
                {
                    telemetry.addLine("(The tag has never been seen)");
                }
                else
                {
                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                    tagToTelemetry(tagOfInterest);
                }

            }

            telemetry.update();
            sleep(20);
        }

        /*
         * The START command just came in: now work off the latest snapshot acquired
         * during the init loop.
         */


        /* Update the telemetry */
        if(tagOfInterest != null)
        {
            telemetry.addLine("Tag snapshot:\n");
            tagToTelemetry(tagOfInterest);
            telemetry.update();
        }
        else
        {
            telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
            telemetry.update();
        }

        armUp(distance(8));
        sleep(15);
        drive.followTrajectory(trajStart);
        //driveForwardPower(distance(4), 0.1);
        sleep(10);
        drive.followTrajectory(trajLeftToFirstJunction);
        drive.followTrajectory(trajForwardToFirstJunction);
        armUp(3850);
        sleep(10);
        //driveForwardPower(distance(4), 0.1);
        drive.followTrajectory(trajSlightlyForwardToFirstJunction);
        sleep(10);
        armDown(400);
        servoOpen();
        sleep(10);
        drive.followTrajectory(trajSlightlyBackwardToFirstJunction);
        sleep(10);
        armDown(3000);
        sleep(10);
        drive.followTrajectory(trajRightToConeStack);
        sleep(10);
        sleep(10);
        drive.followTrajectorySequence(turn90);
        sleep(10);
        drive.followTrajectory(trajLeftToConeStack);
        drive.followTrajectory(trajForwardToConeStack);
        servoClose();
        armUp(900);
        sleep(50);
        drive.followTrajectory(trajAwayConeStack);
        drive.followTrajectorySequence(FaceStraight);
        armUp(1800);
        drive.followTrajectory(trajSlightlyForwardToSecondJunction);
        armDown(300);
        servoOpen();
        sleep(10);
        drive.followTrajectory(trajSlightlyBackwardToSecondJunction);

        /* Actually do something useful */
        if (tagOfInterest == null || tagOfInterest.id == LEFT) {
            drive.followTrajectory(trajZone1);

        } else if (tagOfInterest.id == MIDDLE) { //trajectory
            drive.followTrajectory(trajZone2);

        } else { //trajectory
            drive.followTrajectory(trajZone3);

        }


        /* You wouldn't have this in your autonomous, this is just to prevent the sample from ending */
        while (opModeIsActive()) {sleep(20);}//Delete this line when done testing
    }

    void tagToTelemetry(AprilTagDetection detection)
    {
        if (tagOfInterest == null || tagOfInterest.id == LEFT) {
            telemetry.addLine("/*\n" +
                    "    1  \n" +
                    "   11  \n" +
                    "  111  \n" +
                    "    1  \n" +
                    "    1  \n" +
                    "    1  \n" +
                    "  1111 \n" +
                    "*/");

        } else if (tagOfInterest.id == MIDDLE) {
            telemetry.addLine(String.format(("/*\n" +
                    "  ___  \n" +
                    " |__ \\ \n" +
                    "    ) |\n" +
                    "   / / \n" +
                    "  / /_ \n" +
                    " |____|\n" +
                    "*/")));


        } else { //trajectory
            telemetry.addLine("/*\n" +
                    "  ____ \n" +
                    " /___ \\\n" +
                    "     \\ \\\n" +
                    "    __\\ \\\n" +
                    "   /___\\\n" +
                    "       \\\n" +
                    "  ____/ |\n" +
                    " |_____/ \n" +
                    "*/");
        }

        telemetry.addLine(String.format("\n\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
    }

    public double distance(float inches) {
        //537.6 pulses per rotation
        return inches * (537.6 / (3.75 * 3.141592));
    }

    public void servoOpen() {
        rightClaw.setPosition(0.13);// opens claw
        leftClaw.setPosition(0.83);
    }

    public void servoClose() {
        rightClaw.setPosition(0.45); //closes claw
        leftClaw.setPosition(0.52);
    }


    public void driveForward(double distance) {

        //Reset Encoders
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(0.5);
        rightFront.setPower(0.5);
        leftRear.setPower(0.5);
        rightRear.setPower(0.5);

        while (rightFront.getCurrentPosition() < (distance - 10)) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        //Slowing down to reduce momentum
        leftFront.setPower(0.1);
        rightFront.setPower(0.1);
        leftRear.setPower(0.1);
        rightRear.setPower(0.1);

        while (rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);

    }

    public void driveBackward(double distance) {

        //Reset Encoders
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(-0.5);
        rightFront.setPower(-0.5);
        leftRear.setPower(-0.5);
        rightRear.setPower(-0.5);

        while (-rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);

    }

    public void strafeRight(double distance) {

        //Reset Encoders
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(0.5);
        rightFront.setPower(-0.5);
        leftRear.setPower(-0.5);
        rightRear.setPower(0.5);

        while (-rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);

    }

    public void strafeLeft(double distance) {

        //Reset Encoders
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(-0.5);
        rightFront.setPower(0.5);
        leftRear.setPower(0.5);
        rightRear.setPower(-0.5);

        while (rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);

    }

    public void armUp(double distance) {

        //Reset Encoders
        rightArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightArm.setPower(1);
        leftArm.setPower(1);

        while (rightArm.getCurrentPosition() < distance) {
            telemetry.addData("Arm Encoder", rightArm.getCurrentPosition());
            telemetry.update();
        }

        rightArm.setPower(0);
        leftArm.setPower(0);

        sleep(500);

    }

    public void armDown(double distance) {

        //Reset Encoders
        rightArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftArm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightArm.setPower(-1);
        leftArm.setPower(-1);

        while (-rightArm.getCurrentPosition() < distance) {
            telemetry.addData("Arm Encoder", rightArm.getCurrentPosition());
            telemetry.update();
        }

        rightArm.setPower(0);
        leftArm.setPower(0);

        sleep(500);

    }

    public void driveForwardPower(double distance, double speed) {
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(speed);
        rightFront.setPower(speed);
        leftRear.setPower(speed);
        rightRear.setPower(speed);

        while (rightFront.getCurrentPosition() < (distance - 10)) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        //Slowing down to reduce momentum
        leftFront.setPower(0.1);
        rightFront.setPower(0.1);
        leftRear.setPower(0.1);
        rightRear.setPower(0.1);

        while (rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);
    }
    public void driveBackwardPower(double distance, double speed) { // Positive Speed in Reverse

        //Reset Encoders
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(-speed);
        rightFront.setPower(-speed);
        leftRear.setPower(-speed);
        rightRear.setPower(-speed);

        while (-rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);

    }
    public void strafeLeftPower(double distance, double power) {

        //Reset Encoders
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(-power);
        rightFront.setPower(power);
        leftRear.setPower(power);
        rightRear.setPower(-power);

        while (-rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);

    }
    public void strafeRightPower(double distance, double power) {

        //Reset Encoders
        leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftRear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftFront.setPower(power);
        rightFront.setPower(-power);
        leftRear.setPower(-power);
        rightRear.setPower(power);

        while (rightFront.getCurrentPosition() < distance) {
            telemetry.addData("Left Encoder", rightFront.getCurrentPosition());
            telemetry.update();
        }

        leftFront.setPower(0);
        rightFront.setPower(0);
        leftRear.setPower(0);
        rightRear.setPower(0);

        sleep(500);

    }
}