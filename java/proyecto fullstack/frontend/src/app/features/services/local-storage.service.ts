import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {

  /**
   * Loads value from local storage.
   * @param key Key to be used to load the value.
   * @returns Value loaded from local storage.
   */
  load(key: string): string | null | undefined {
    let value = localStorage.getItem(key);
    value = value === '' ? null : value;
    return value;
  }


  /**
   * Checks if the given key exists in local storage.
   * @param key  Key to be checked.
   * @returns True if the key exists in local storage, false otherwise.
   */
  keyExists(key: string): boolean {
    return localStorage.getItem(key) !== null;
  }

  /**
   * Loads value from local storage.
   * @param key Key to be used to load the value.
   * @returns Value loaded from local storage.
   */
  removeItem(key: string): void {
    localStorage.removeItem(key);
  }

  /**
   * Save the given value to the local storage
   * using the given key.
   * @param key Key to be used to save the value.
   * @param value Value to be saved.
   */
  save(key: string, value: string | null | undefined) {
    // Cannot save null in local storage, so in case of null, change it to empty string
    if (value == null) {
      value = '';
    }
    localStorage.setItem(key, value);
  }

  /**
   * Save the given value to the local storage
   * using the given key.
   * @param key Key to be used to save the value.
   * @param value Value to be saved.
   */
  saveArray(key: string, value: any[] | null | undefined) {
    // Cannot save null in local storage, so in case of null, change it to empty string
    if (value == null) {
      localStorage.setItem(key, '');
    }
    else {
      localStorage.setItem(key, JSON.stringify(value));
    }
  }

  /**
   * Loads the value for the given key from local storage. Then finds that value in the
   * elements array where the property valueField matches the value loaded from local storage.
   * @param localStorageKey Local storage key.
   * @param elements Array of elements where the value loaded from local storage will be searched.
   * @param valueField Field to be used to compare the value loaded from local storage.
   * @returns Value found in the elements array.
   */
  loadForArray(
    localStorageKey: string,
    elements: any[],
    valueField: string
  ): any {
    let element = null;
    try {
      const localStorageValue = this.load(localStorageKey);
      element = elements.find((item) => item[valueField] == localStorageValue);

      // Check that a valid value was found
      if (localStorageValue === null || !element) {
        throw new Error('Invalid value was found');
      }
    } catch (error) {
      element = null as any;
      this.save(localStorageKey, element);
    }
    return element?.[valueField];
  }


  /**
   * Loads the value for the given key from local storage. Then finds those values in the
   * elements array where the property valueField matches the values loaded from local storage.
   * @param localStorageKey Local storage key.
   * @param elements Array of elements where the value loaded from local storage will be searched.
   * @param valueField Field to be used to compare the value loaded from local storage.
   * @returns Value found in the elements array.
   */
  loadMultipleForArray(
    localStorageKey: string,
    elements: any[],
    valueField: string
  ): any {
    let array = null;
    try {
      let localStorageValue = this.load(localStorageKey);
      
      if (localStorageValue === null || localStorageValue === undefined) {
        return null;
      }

      localStorageValue = JSON.parse(localStorageValue);
       
      // Get the elements that match the values in the local storage
      array = elements.filter((item) => localStorageValue && localStorageValue.includes(item[valueField]));
     
      // Check that a valid value was found
      if (!array.length) {
        throw new Error('Invalid value was found');
      }
    } catch (error) {
      array = null as any;
      this.save(localStorageKey, array);
    }

    return array ? array.map((item: any) => item[valueField]) : null;
  }
}
