import { Component } from '@angular/core';
import { Course } from '../../dtos/course.dto';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../../services/course.service';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { LocalStorageService } from '../../../services/local-storage.service';

@Component({
  selector: 'app-course-listing',
  standalone: true,
  imports: [CommonModule, TableModule],
  templateUrl: './course-listing.component.html',
  styleUrl: './course-listing.component.css'
})
export class CourseListingComponent {

  // Fields

  columns: any[] = [];
  selectedColumns: any[] = [];
  courses: Course[] = []
  selectedCourses: Course[] = [];
  totalRecords: number = 0;
  processing = false;
  maxRows = 20;
  userAccountId: number;
  companyId: number;

  // Constructor

  constructor(
    private router: Router,
    private courseService: CourseService,
    private route: ActivatedRoute,
    private localStorageService: LocalStorageService
  ) {
    this.userAccountId = Number(this.route.snapshot.paramMap.get('userAccountId'));
    this.companyId = Number(this.localStorageService.load('selectedCompanyId'));

  }

  // Logic

  /**
   * Initializes the component.
   */
  ngOnInit(): void {
    this.loadColumns();
    this.prepareAndLoadData();
  }

  /**
   * Loads columns.
   */
  private loadColumns() {
    this.columns = [
      {
        field: 'id',
        header: 'id curso',
        filterType: 'text',
        pipe: null,
        classes: '',
        type: 'plain',
        headerType: 'plain',
      },
      {
        field: 'name',
        header: 'nombre curso',
        filterType: 'text',
        pipe: null,
        classes: '',
        type: 'plain',
        headerType: 'plain',
      }
    ];

    this.selectedColumns = this.columns;
  }

  /**
   * Preload the data.
   */
  private prepareAndLoadData() {
    this.loadCourses();
  }


  /**
   * Loads courses listing from backend.
   * @param event TableLazyLoadEvent.
   */
 loadCourses() {
  this.processing = true;

  this.courseService.findCourses().subscribe((response) => {
    this.selectedCourses = response;
    this.totalRecords = response.length;
    this.processing = false;
  });
}


  /**
   * Redirects to create course.
   */
  redirectToCreateCourse() {
    this.router.navigate(['/app', 'courses', 'new']);
  }
}
