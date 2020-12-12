package com.fireextinguisher.utils;

import android.os.Environment;

import java.io.File;

public class Constant {

    public static final int PERMISSION_REQUEST_CAMERA = 1001;

    public static final int FACING_BACK = 0;
    public static final int FACING_FRONT = 1;

    public static final int FLASH_OFF = 0;
    public static final int FLASH_ON = 1;
    public static final int FLASH_AUTO = 2;
    public static final int FLASH_TORCH = 3;

    public static final int FOCUS_OFF = 0;
    public static final int FOCUS_CONTINUOUS = 1;
    public static final int FOCUS_TAP = 2;
    public static final int FOCUS_TAP_WITH_MARKER = 3;
    public static final String WEB_URL = "weburl";
    public static final String WEB_SUBJECT = "websubject";
    public static final String email = "email";
    public static final String password = "password";
    public static final String userId = "userId";
    //https://webmuse.in/strap/assets/pimage/IMG_20200528_123807.jpg
    public static final String status = "status";
    public static final String object = "object";
    public static final String mobile = "mobile";
    public static final String token = "token";
    public static final String empid = "empid";
    public static final String empId = "empId";
    public static final String id = "id";
    public static final String message = "message";
    public static final String KEY_CAMERA_PERMISSION_GRANTED = "CAMERA_PERMISSION_GRANTED";
    public static final String modelNo = "modelNo";
    public static final String productId = "productId";
    public static final String clientName = "clientName";
    public static final String clientId = "clientId";
    public static final String pumpType = "pumpType";
    public static final String SEND = "send.jpg";
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public static final String MYFOLDER_DIR_NAME = ROOT_DIR + File.separator + "M-Fire";
    public static final String MYFOLDER_DIR_NAME_SEPARATOR = MYFOLDER_DIR_NAME + File.separator;
    public static final String MYFOLDER = "M-Fire";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    private static final String MAIN_URL = "https://webmuse.in/";
    public static final String SERVER_URL = MAIN_URL + "strap/api/v1/";
    public static final String WEB_T_C = MAIN_URL + "more/terms-condition.html";
    public static final String WEB_POLICY = MAIN_URL + "more/policy.html";
    public static final String IMAGE_PATH = MAIN_URL + "strap/";

    public static class ProductInfo {
        public static final String location = "location";
        public static final String f_e_no = "feNo";
        public static final String fe_type_label = "feType";
        public static final String capacity_label = "capacity";
        public static final String mfg_year_label = "mfgYear";
        public static final String empty_cylinder_pressure = "emptyCylinderPressure";
        public static final String full_cylinder_pressure = "fullCylinderPressure";
        public static final String net_cylinder_pressure = "netPressure";
        public static final String last_date_refill_label = "lastDateRefilling";
        public static final String due_date_refill_label = "dueDateRefiiling";
        public static final String last_date_hpt_label = "lastDateHpt";
        public static final String due_date_hpt_label = "dueDateHpt";
        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";
    }

    public static class FireHydrantInfo {
        public static final String location = "location";
        public static final String hose_pipe = "hosePipe";
        public static final String hydrant_valve = "hydrantValve";
        public static final String black_cap = "blackCap";
        public static final String shunt_wheel = "shuntWheel";
        public static final String hose_box = "hoseBox";
        public static final String hoses = "hoses";
        public static final String glasses = "glasses";
        public static final String branch_pipe = "branchPipe";
        public static final String keys = "keys";
        public static final String glass_hammer = "glassHammer";
        public static final String observation = "observation";
        public static final String action = "action";
        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";
    }

    public static class HoseReelInfo {
        public static final String location = "location";
        public static final String observation = "observation";
        public static final String action = "action";
        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";
        public static final String hose_reel = "hoseReel";
        public static final String shut_off_nozzel = "shutOffNozzel";
        public static final String ball_valve = "ballValve";
        public static final String jubli_clip = "jubliClip";
        public static final String conecting_ruber_hose = "connectingRubberHose";

    }

    public static class FireBucket {
        public static final String location = "location";
        public static final String observation = "observation";
        public static final String action = "action";
        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";
        public static final String number_of_fire_bucket = "numberOfFireBuckets";
        public static final String buckets = "buckets";
        public static final String stand = "stand";
        public static final String sand = "sand";

    }

    public static class FirePump {
        public static final String location = "location";
        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";

        public static final String HP = "hp";
        public static final String Head = "head";
        public static final String KW = "kw";
        public static final String pumpNo = "pumpNo";
        public static final String pumpType = "pumpType";
        public static final String RPM = "rpm";
        public static final String motorNo = "motorNo";
    }


    public static class PortableMonitor {
        public static final String location = "location";
        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";

        public static final String rotation = "rotation";
        public static final String capacity = "capacity";
        public static final String flow = "flow";
        public static final String pressure = "pressure";
        public static final String size = "size";
        public static final String typeOfMonitor = "typeOfMonitor";
        public static final String mocBody = "mocBody";
        public static final String throwRange = "throwRange";
        public static final String foamTank = "foamTank";
        public static final String foamInductor = "foamInductor";
        public static final String handleRotationWheel = "handleRotationWheel";
        public static final String foamInductorPipe = "foamInductorPipe";
        public static final String nozzleType = "nozzleType";
        public static final String operationManualElectric = "operationManualElectric";
    }

    public static class FireDetectionPanel {
        public static final String location = "location";
        //        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        //        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";

        public static final String make = "make";
        public static final String siren = "siren";
        public static final String cables = "cables";
        public static final String specsOfPanel = "specsOfPanel";
        public static final String typeOfSystem = "typeOfSystem";
        public static final String loopsZone = "loopsZone";
        public static final String repeaterPanel = "repeaterPanel";
        public static final String isolatorModule = "isolatorModule";
        public static final String heatDetector = "heatDetector";
        public static final String flameDetector = "flameDetector";
        public static final String smokeDetector = "smokeDetector";
        public static final String multiDetector = "multiDetector";
        public static final String gasDetector = "gasDetector";
        public static final String beamDetector = "beamDetector";
        public static final String manualCallPoint = "manualCallPoint";
        public static final String hooterCumSounder = "hooterCumSounder";
        public static final String zoneMonitorModule = "zoneMonitorModule";
        public static final String monitorModule = "monitorModule";
        public static final String controlModule = "controlModule";
        public static final String powerSupplyUnit = "powerSupplyUnit";
        public static final String zenerBarrier = "zenerBarrier";
        public static final String batteryBackup = "batteryBackup";
    }

    public static class ControlValve {
        public static final String location = "location";
        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";

        public static final String moc = "moc";
        public static final String size = "size";
        public static final String spindle = "spindle";
        public static final String gasket = "gasket";
        public static final String pressure = "pressure";
        public static final String flow = "flow";
        public static final String typeOfValve = "typeOfValve";
        public static final String wheelLever = "wheelLever";
        public static final String glandPacking = "glandPacking";
        public static final String drainValve = "drainValve";
        public static final String pressureGuage = "pressureGuage";
        public static final String testValve = "testValve";
        public static final String soleniodValveActuator = "soleniodValveActuator";
        public static final String internalDiscFlap = "internalDiscFlap";
        public static final String gongBell = "gongBell";
    }

    public static class SuppressionSystem {
        public static final String location = "location";
//        public static final String spare_part_label = "sparePartRequired";
        public static final String remarks = "remarks";
//        public static final String spare_part_item_label = "sparePartItemRequired";
        public static final String clientName = "clientName";

        public static final String make = "make";
        public static final String manifold = "manifold";
        public static final String piping = "piping";
        public static final String siren = "siren";
        public static final String cables = "cables";
        public static final String specsOfSupressionWSystem = "specsOfSupressionWSystem";
        public static final String specsOfPanelAndMake = "specsOfPanelAndMake";
        public static final String typeOfSystem = "typeOfSystem";
        public static final String loopsZone = "loopsZone";
        public static final String modelNoSuppresion = "modelNoSuppresion";
        public static final String capacityOfClyinder = "capacityOfClyinder";
        public static final String noOfCylinders = "noOfCylinders";
        public static final String emptyWeight = "emptyWeight";
        public static final String fullWeight = "fullWeight";
        public static final String suppressionGasFilled = "suppressionGasFilled";
        public static final String pressureGauageReading = "pressureGauageReading";
        public static final String electromagneticActuator = "electromagneticActuator";
        public static final String pneumaticActuator = "pneumaticActuator";
        public static final String pressureSupervisorySwitch = "pressureSupervisorySwitch";
        public static final String flexibleDischargeHose = "flexibleDischargeHose";
        public static final String flexibleActuatorHose = "flexibleActuatorHose";
        public static final String nozzlesSuppresion = "nozzlesSuppresion";
        public static final String abortSwitch = "abortSwitch";
        public static final String heatDetector = "heatDetector";
        public static final String flameDetector = "flameDetector";
        public static final String smokeDetector = "smokeDetector";
        public static final String multiDetector = "multiDetector";
        public static final String gasDetector = "gasDetector";
        public static final String vesdaDetectorPanel = "vesdaDetectorPanel";
        public static final String manualCallPoint = "manualCallPoint";
        public static final String hooterCumSounder = "hooterCumSounder";
        public static final String zoneMonitorModule = "zoneMonitorModule";
        public static final String monitorModule = "monitorModule";
        public static final String controlModule = "controlModule";
        public static final String powerSupplyUnit = "powerSupplyUnit";
        public static final String manualGasReleaseSwitch = "manualGasReleaseSwitch";
        public static final String specialDetector = "specialDetector";
        public static final String batteryBackup = "batteryBackup";
    }

}
