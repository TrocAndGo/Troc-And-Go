import { CommonModule } from '@angular/common';
import { Component, forwardRef, Input, OnInit } from '@angular/core';
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
export class DropdownButtonComponent implements OnInit, ControlValueAccessor {
  @Input() text: string = 'Click me';
  @Input() color: string = 'primary';
  @Input() items: string[] = [];
  @Input() itemValues: string[] = [];
  @Input() disabled: boolean = false;
  @Input() showDefaultItem: boolean = true;

  valueText: string | null = null;
  value: string | null = null;
  onChange: (value: string | null) => void = () => {};
  onTouched: () => void = () => {};
  dropdownOpen: boolean = false;

  ngOnInit() {
    if (this.itemValues.length === 0) {
      this.itemValues = this.items;
    }
  }

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  selectItem(itemText: string | null, itemValue: string | null) {
    // console.log('Item selected:', item);
    this.valueText = itemText;
    this.writeValue(itemValue);
    this.onChange(itemValue);
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
    const isValid = this.itemValues.includes(value) || value === null;
    return isValid ? null : {invalid: true};
  }
}
