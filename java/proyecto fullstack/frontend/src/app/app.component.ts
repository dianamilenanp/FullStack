import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CourseListingComponent } from "./features/courses/components/course-listing/course-listing.component";
import { MenuComponent } from './features/canvas/menu/menu.component';

@Component({
  selector: 'app-root',
  imports: [ CourseListingComponent, MenuComponent],
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'frontend';
}
