import { Course } from "./course.dto";

/**
 * Data to create a course.
 * 
 */
export class CourseListingResponse {

  
  records: Course[] = [];
  totalRecords: number = 0;
  
}

