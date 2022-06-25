import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DrivingInterface.*;
import DrivingInterface.DrivingInterface.ObstaclesInfo;

public class MyCar {
	// 사고 대처를 위한 변수
	boolean is_accident = false;
	int accident_count = 0;
	int recovery_count = 0;
	
    boolean is_debug = false;
    static boolean enable_api_control = true; // true(Controlled by code) /false(Controlled by keyboard)

    public void control_driving(boolean a1, float a2, float a3, float a4, float a5, float a6, float a7, float a8,
                                float[] a9, float[] a10, float[] a11, float[] a12) {

        // ===========================================================
        // Don't remove this area. ===================================
        // ===========================================================
        DrivingInterface di = new DrivingInterface();
        DrivingInterface.CarStateValues sensing_info = di.get_car_state(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12);
        // ===========================================================

        if(is_debug) {
            System.out.println("=========================================================");
            System.out.println("[MyCar] to middle: " + sensing_info.to_middle);

            System.out.println("[MyCar] collided: " + sensing_info.collided);
            System.out.println("[MyCar] car speed: " + sensing_info.speed + "km/h");

            System.out.println("[MyCar] is moving forward: " + sensing_info.moving_forward);
            System.out.println("[MyCar] moving angle: " + sensing_info.moving_angle);
            System.out.println("[MyCar] lap_progress: " + sensing_info.lap_progress);

            StringBuilder forward_angles = new StringBuilder("[MyCar] track_forward_angles: ");
            for (Float track_forward_angle : sensing_info.track_forward_angles) {
                forward_angles.append(track_forward_angle).append(", ");
            }
            System.out.println(forward_angles);

            StringBuilder to_way_points = new StringBuilder("[MyCar] distance_to_way_points: ");
            for (Float distance_to_way_point : sensing_info.distance_to_way_points) {
                to_way_points.append(distance_to_way_point).append(", ");
            }
            System.out.println(to_way_points);

            StringBuilder forward_obstacles = new StringBuilder("[MyCar] track_forward_obstacles: ");
            for (DrivingInterface.ObstaclesInfo track_forward_obstacle : sensing_info.track_forward_obstacles) {
                forward_obstacles.append("{dist:").append(track_forward_obstacle.dist)
                        .append(", to_middle:").append(track_forward_obstacle.to_middle).append("}, ");
            }
            System.out.println(forward_obstacles);

            StringBuilder opponent_cars = new StringBuilder("[MyCar] opponent_cars_info: ");
            for (DrivingInterface.CarsInfo carsInfo : sensing_info.opponent_cars_info) {
                opponent_cars.append("{dist:").append(carsInfo.dist)
                        .append(", to_middle:").append(carsInfo.to_middle)
                        .append(", speed:").append(carsInfo.speed).append("km/h}, ");
            }
            System.out.println(opponent_cars);

            System.out.println("=========================================================");
        }

        // ===========================================================
        // Area for writing code about driving rule ==================
        // ===========================================================
        // Editing area starts from here
        
//        float set_brake = 0.0f;
//        float set_throttle = 0.73f;
//        
//        /**
//         * angle_num : 현재 차량 속도를 기준으로 얻을 전방의 커브 정보의 인덱스 값 (45는 적절한 보정 값)
//         * sensing_info.speed : 현재 속도
//         */
//        int angle_num = (int) (sensing_info.speed / 45);
//        
//        /**
//         * ref_angle : track_forward_angles 배열의 angle_num번째  index 값
//         * sensing_info.track_forward_angles : 차량 가준 전방의 커브 정보  (가까운 순으로 배열)
//         */
//        float ref_angle = sensing_info.track_forward_angles.get(angle_num);
//        
//        /**
//         * middle_add : 도로의 중앙으로 주행 보정 (70은 상수 보정 값), -1은 조향의 반대 방향 적용을 위함
//         * sensing_info.to_middle : 도로 중앙으로부터 차량까지 떨어진 거리
//         */
//        float middle_add = (sensing_info.to_middle / 70) * -1;
//        
//        // 장애물 고려
//        ArrayList<DrivingInterface.ObstaclesInfo> obstacles_list = sensing_info.track_forward_obstacles;
//        if(obstacles_list.size() > 0) {
//        	//////////////////////// 여러 개 장애물 회피 ////////////////////////
//        	int road_width = (int) ((sensing_info.half_road_limit - 1.25) * 2);
//        	boolean[] is_blocked = new boolean[road_width]; // 도로의 폭만큼 배열 생성(1칸 = 1m)
//        	int same_pos_obs = 0;
//        	float nearest_obs = obstacles_list.get(0).dist;
//        	
//        	for(int i = 0; i < obstacles_list.size(); i++) {
//        		ObstaclesInfo cur_obs = obstacles_list.get(i);
//				if(nearest_obs - 1.5 < cur_obs.dist && cur_obs.dist < nearest_obs + 1.5) {
//					same_pos_obs++;
//					
//					for(int j = (int) (cur_obs.to_middle + (road_width / 2) - 1); j <= (int) (cur_obs.to_middle + (road_width / 2) + 1); j++) {
//						if(0 > j || road_width <= j) continue; // index 유효 범위 체크
//						else is_blocked[j] = true;
//					}
//					
//				} else {
//					break;
//				}
//			}
//        	/////////////////////////////////////////////////////////////
//        	  	
//        	DrivingInterface.ObstaclesInfo fwd_obstacles = obstacles_list.get(0);
//        	
//        	// 전방 60m, 도로 내의 장애물만 고려
//        	if(same_pos_obs == 1 && fwd_obstacles.dist < 60 && fwd_obstacles.dist > 0 && Math.abs(fwd_obstacles.to_middle) < 8.0) {
//            	float avoid_width = 2.7f;
//            	float diff = fwd_obstacles.to_middle - sensing_info.to_middle;
//            	if(Math.abs(diff) < avoid_width) {
//            		ref_angle = (float) (Math.abs(Math.atan((diff-avoid_width) / fwd_obstacles.dist) * 57.29579));
//            		middle_add = 0;
//            		
//            		// 장애물이 오른쪽에 있을 때, 핸들을 왼쪽으로 꺾음
//            		if(diff > 0) ref_angle *= -1;
//            	}
//        	} else if(same_pos_obs > 1 && fwd_obstacles.dist < 60 && fwd_obstacles.dist > 0 && Math.abs(fwd_obstacles.to_middle) < 8.0) {
//        		int start = -1;
//        		int end = -1;
//        		int start_temp = -1;
//        		int end_temp = -1;
//        		int max_gap = 0;
//        		int gap_cnt = 0;
//        		for(int i = 0; i < is_blocked.length; i++) {
//					if(!is_blocked[i]) {
//						if(gap_cnt <= 0) start_temp = i - (road_width / 2);
//						end_temp = i - (road_width / 2) + 1;
//						gap_cnt++;
//					} else {
//						gap_cnt = 0;
//					}
//					
//					if(gap_cnt > max_gap) {
//						start = start_temp;
//						end = end_temp;
//						max_gap = gap_cnt;
//					}
//				}
//        		
//        		float pos = ((start + end) / 2);
//        		ref_angle = (float) (Math.abs(Math.atan(pos / fwd_obstacles.dist) * 57.29579));
//        		middle_add = 0;
//        		if(pos < 0) ref_angle *= -1;
//        	}
//        }
//        
//        // 상대 차량 고려
//        if(sensing_info.opponent_cars_info.size() > 0) {
//        	DrivingInterface.CarsInfo opp_car = sensing_info.opponent_cars_info.get(0);
//        	
//        	if(opp_car.dist < 20 && opp_car.dist > -5) { 	
//		    	float offset = (8 - Math.abs(opp_car.to_middle)) / 2;
//		    	if(opp_car.to_middle > sensing_info.to_middle) {
//		    		middle_add = ((sensing_info.to_middle + offset) / 70) * -1;
//		    	} else {
//		    		middle_add =  ((sensing_info.to_middle - offset) / 70) * -1;
//		    	}
//        	}
//        }
//        
//        /**
//         * set_steering : car_controls.steering에 최종 대입할 값
//         * (전방의 커브 값 - 현재 차량의 진행 각) / 속도에 따른 보정 값
//         * sensing_info.moving_angle : 차량의 현재 진행 방향
//         */
//        float set_steering = (ref_angle - sensing_info.moving_angle) / (180 - sensing_info.speed);
//        
//        set_steering += middle_add;
//        
//        
//        // 곡선 구간 커브
//        boolean full_throttle = true;
//        boolean emergency_brake = false;
//        
//        int road_range = (int) (sensing_info.speed / 30);
//        for(int i = 0; i < road_range; i++) {
//			float fwd_angle = Math.abs(sensing_info.track_forward_angles.get(i));
//			if(fwd_angle > 50) {
//				full_throttle = false;
//			}
//			if(fwd_angle > 90) {
//				emergency_brake = true;
//				break;
//			}
//		}
//        
//        if(!full_throttle) {
//        	if(sensing_info.speed > 130) {
//        		set_throttle = 0.5f;
//        	}
//        	if(sensing_info.speed > 120) {
//        		set_brake = 1;
//        	}
//        }
//        
//        if(emergency_brake) {
//        	if(set_steering > 0) {
//        		set_steering += 0.3;
//        	} else {
//        		set_steering -= 0.3;
//        	}
//        }
//        
        
        
        float set_brake = 0.0f;
        float set_throttle = sensing_info.lap_progress > 93.0 ? 1 : 0.84f;
        
        
        float[][] way_points = new float[10][2];
        float x = 0.0f;
        float y = (float) Math.sqrt(Math.pow(sensing_info.distance_to_way_points.get(0), 2) - Math.pow(sensing_info.to_middle, 2));
        way_points[0][1] = y;
        
        for(int i = 1; i < way_points.length; i++) {
			x = (float) (way_points[i-1][0] + (10 * Math.sin(Math.toRadians(sensing_info.track_forward_angles.get(i-1)))));
			y = (float) (way_points[i-1][1] + (10 * Math.cos(Math.toRadians(sensing_info.track_forward_angles.get(i-1)))));
			way_points[i][0] = x;
			way_points[i][1] = y;
		}
        
        int ref_idx = Math.max(1, Math.min(9, (int) (sensing_info.speed / 15)));
        float target_x = way_points[ref_idx][0];
        float target_y = way_points[ref_idx][1];
        
       	float set_steering = (float) ((Math.atan((target_x - sensing_info.to_middle) / target_y) - Math.toRadians(sensing_info.moving_angle)) * 0.41);
       	
       	ArrayList<DrivingInterface.ObstaclesInfo> forward_obstacles = new ArrayList<DrivingInterface.ObstaclesInfo>();
       	
       	for(int i = 0; i < sensing_info.track_forward_obstacles.size(); i++) {
       		DrivingInterface.ObstaclesInfo obs = sensing_info.track_forward_obstacles.get(i);
       		if(obs.dist > 0 && obs.dist < (sensing_info.speed * 0.6) && Math.abs(obs.to_middle) < sensing_info.half_road_limit + 1.0) {
       			forward_obstacles.add(obs);
       		}
       	}
       	
       	if(forward_obstacles.size() > 0) {
       		int road_width = (int) ((sensing_info.half_road_limit - 1.25) * 2);
        	boolean[] is_blocked = new boolean[road_width]; // 도로의 폭만큼 배열 생성(1칸 = 1m)
        	int same_pos_obs = 0;
        	float nearest_obs = forward_obstacles.get(0).dist;
        	
        	for(int i = 0; i < forward_obstacles.size(); i++) {
        		ObstaclesInfo cur_obs = forward_obstacles.get(i);
				if(nearest_obs - 1.5 < cur_obs.dist && cur_obs.dist < nearest_obs + 1.5) {
					same_pos_obs++;
					
					for(int j = (int) (cur_obs.to_middle + (road_width / 2) - 1); j <= (int) (cur_obs.to_middle + (road_width / 2) + 1); j++) {
						if(0 > j || road_width <= j) continue; // index 유효 범위 체크
						else is_blocked[j] = true;
					}
				} else {
					break;
				}
        	}
       		
        	if(same_pos_obs == 1) {
	       		//System.out.println("obs: " + forward_obstacles.get(0).to_middle + " : " + forward_obstacles.get(0).dist);

	       		ref_idx = Math.max(1, Math.min(9, (int) (forward_obstacles.get(0).dist / 10)));
	
	       		// diff가 음수이면 장애물이 차보다 왼쪽에 있음
	       		// diff가 양수이면 장애물이 차보다 오른쪽에 있음
	       		float diff = forward_obstacles.get(0).to_middle - sensing_info.to_middle;
	       		
	       		// 장애물의 범위 내에 있는 경우
	       		if(Math.abs(diff) < 2.7) {
	       			if(diff > 0) { // 장애물이 오른쪽에 있음
	       	    		target_x = way_points[ref_idx][0] + (float) (forward_obstacles.get(0).to_middle - 2.7);
	       	       		target_y = forward_obstacles.get(0).dist;
	       			} else { // 장애물이 왼쪽에 있음
	       	    		target_x = way_points[ref_idx][0] + (float) (forward_obstacles.get(0).to_middle + 2.7);
	       	       		target_y = forward_obstacles.get(0).dist;
	       			}
	       		}
	       		
	       		set_steering = (float) ((Math.atan((target_x - sensing_info.to_middle) / target_y) - Math.toRadians(sensing_info.moving_angle)) * 0.8);
        	} else if(same_pos_obs > 1) {
        		int start = -1;
        		int end = -1;
        		int start_temp = -1;
        		int end_temp = -1;
        		int max_gap = 0;
        		int gap_cnt = 0;
        		for(int i = 0; i < is_blocked.length; i++) {
					if(!is_blocked[i]) {
						if(gap_cnt <= 0) start_temp = i - (road_width / 2);
						end_temp = i - (road_width / 2) + 1;
						gap_cnt++;
					} else {
						gap_cnt = 0;
					}
					
					if(gap_cnt > max_gap) {
						start = start_temp;
						end = end_temp;
						max_gap = gap_cnt;
					}
				}
        		
        		target_x = ((start + end) / 2);
   	       		target_y = forward_obstacles.get(0).dist;
	       		set_steering = (float) ((Math.atan((target_x - sensing_info.to_middle) / target_y) - Math.toRadians(sensing_info.moving_angle)) * 0.8);
        	}
        	
//       		target_x = way_points[ref_idx][0] + (forward_obstacles.get(0).to_middle - 2);
//       		System.out.println(ref_idx + " & " + way_points[ref_idx][0] + " : " + way_points[ref_idx][1]);
//       		target_y = forward_obstacles.get(0).dist;
//
//       		System.out.println("tar: " + target_x + " : " + target_y);
       			
       	}
       	
       	
      // 곡선 구간 커브
      boolean full_throttle = true;
      boolean emergency_brake = false;
      
      int road_range = (int) (sensing_info.speed / 30);
      for(int i = 0; i < road_range; i++) {
			float fwd_angle = Math.abs(sensing_info.track_forward_angles.get(i));
			if(fwd_angle > 50) {
				full_throttle = false;
			}
			if(fwd_angle > 90) {
				emergency_brake = true;
				break;
			}
		}
      
      if(!full_throttle) {
      	if(sensing_info.speed > 130) {
      		set_throttle = 0.5f;
      	}
      	if(sensing_info.speed > 120) {
      		set_brake = 1;
      	}
      }
      
      if(emergency_brake) {
      	if(set_steering > 0) {
      		set_steering += 0.3;
      	} else {
      		set_steering -= 0.3;
      	}
      }
       	
     // 충돌 recovery
        if(sensing_info.lap_progress > 0.5 && !is_accident && (sensing_info.speed < 1.0 && sensing_info.speed > -1.0)) {
        	accident_count += 1;
        }
        
        if(accident_count > 5) {
        	is_accident = true;
        }
        
        if(is_accident) {
        	set_steering = 0.02f;
        	set_brake = 0;
        	set_throttle = -1;
        	recovery_count += 1;
        }
        
        if(recovery_count > 15) {
        	is_accident = false;
        	recovery_count = 0;
        	accident_count = 0;
        	set_steering = 0;
        	set_brake = 0;
        	set_throttle = 0;
        }
    
        // Moving straight forward
        car_controls.steering = set_steering;
        car_controls.throttle = set_throttle;
        car_controls.brake = set_brake;
        

        if(is_debug) {
            System.out.println("[MyCar] steering:" + car_controls.steering + ", throttle:" + car_controls.throttle + ", brake:" + car_controls.brake);
        }

        //
        // Editing area ends
        // =======================================================
    }

    // ===========================================================
    // Don't remove below area. ==================================
    // ===========================================================
    public native int StartDriving(boolean enable_api_control);

    static MyCar car_controls;

    float throttle;
    float steering;
    float brake;

    static {
        System.loadLibrary("DrivingInterface/DrivingInterface");
    }

    public static void main(String[] args) {
        System.out.println("[MyCar] Start Bot! (JAVA)");

        car_controls = new MyCar();
        int return_code = car_controls.StartDriving(enable_api_control);

        System.out.println("[MyCar] End Bot! (JAVA), return_code = " + return_code);

        System.exit(return_code);
    }
    // ===========================================================
}
