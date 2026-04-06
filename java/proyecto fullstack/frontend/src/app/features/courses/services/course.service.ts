import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CourseListingResponse } from '../dtos/course-listing-response.dto';
import { Course } from '../dtos/course.dto';

/**
 * Services related with courses.
 */

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  
  private apiUrl = 'http://localhost:8080/';

  // Logic

  constructor(
    private httpClient: HttpClient,
  ) {}

  /**
   * Returns the list of courses.
   * 
   * @returns List of courses.
   */
  findCourses(): Observable<Course[]> {
    const url = `${this.apiUrl}courses`;
    return this.httpClient.get<Course[]>('mocks/courses.json');
  }
  
}
