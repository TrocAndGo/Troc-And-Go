import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css'
})
export class PaginationComponent {
  @Input() totalPages: number = 10;
  @Input() currentPage: number = 0;
  @Input() maxPagesToDisplay: number = 10;

  @Output() onPageChange = new EventEmitter<number>();

  get pages(): number[] {
    const half = Math.floor(this.maxPagesToDisplay / 2);
    let start = Math.max(0, this.currentPage - half);
    let end = Math.min(this.totalPages, this.currentPage + half);

    if (end - start < this.maxPagesToDisplay) {
      if (start === 0) {
        end = Math.min(this.totalPages, start + this.maxPagesToDisplay);
      } else if (end === this.totalPages) {
        start = Math.max(0, end - this.maxPagesToDisplay);
      }
    }

    return Array.from({ length: end - start }, (_, i) => start + i);
  }

  changePage(page: number): void {
    if (page !== this.currentPage)
      this.onPageChange.emit(page);
  }
}
