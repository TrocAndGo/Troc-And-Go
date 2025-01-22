import { CommonModule } from '@angular/common';
import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALIDATORS, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-dropdown-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dropdown-button.component.html',
  styleUrl: './dropdown-button.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: forwardRef(() => DropdownButtonComponent),
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: forwardRef(() => DropdownButtonComponent),
    },
  ],
})
export class DropdownButtonComponent implements ControlValueAccessor {
  @Input() text: string = 'Click me';
  @Input() color: string = 'primary';
  @Input() items: string[] = [];
  @Input() disabled: boolean = false;

  value: string | null = null;
  onChange: (value: string | null) => void = () => {};
  onTouched: () => void = () => {};
  dropdownOpen: boolean = false;

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  selectItem(item: string | null) {
    // console.log('Item selected:', item);
    this.writeValue(item);
    this.onChange(item);
    this.dropdownOpen = false;
  }

  /* ControlValueAccessor methods */
  writeValue(value: string | null): void {
    this.value = value;
  }
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
  validate({value}: FormControl) {
    const isValid = this.items.includes(value) || value === null;
    return isValid ? null : {invalid: true};
  }
}
