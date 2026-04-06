import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { LocalStorageService } from '../../../services/local-storage.service';

@Component({
  selector: 'app-student-listing',
  imports: [],
  templateUrl: './student-listing.component.html',
  styleUrl: './student-listing.component.css'
})
export class StudentListingComponent implements OnInit {

  @Output() onChange = new EventEmitter<any>();

  @Input() selectFirst: boolean = true;
  @Input() control: FormControl = new FormControl(null);


  private readonly COMPANY_KEY = 'selectedCompanyId';

  companies!: any[];
  selectedCompany?: any;

  constructor(
    private issuerCompanySelectorService: any,
    private localStorageService: LocalStorageService
  ) {

  }

  ngOnInit(): void {
    this.issuerCompanySelectorService.loadIssuerCompanies()
      .subscribe(response => {
        this.companies = response.companies;

        if (!this.companies || this.companies.length === 0) {
          return;
        }

        const storedCompanyId = this.localStorageService.loadForArray(this.COMPANY_KEY, this.companies, 'id');

        if (storedCompanyId) {
          this.selectedCompany = this.companies.find(
            c => c.id === storedCompanyId
          );
        } else if (this.selectFirst) {
          this.selectedCompany = this.companies[0];
          this.localStorageService.save(this.COMPANY_KEY, this.selectedCompany.id.toString());
        }

        if (this.selectedCompany) {
          this.onChange.emit(this.selectedCompany);
        }
      });
  }

  onCompanyChange(company: any): void {
    this.selectedCompany = company;

    this.localStorageService.save(
      this.COMPANY_KEY,
      company.id.toString()
    );

    this.onChange.emit(company);
  }


}

