import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { UploadFileService } from '../upload-file.service';

@Component({
  selector: 'list-upload',
  templateUrl: './list-upload.component.html',
  styleUrls: ['./list-upload.component.css']
})
export class ListUploadComponent implements OnInit {

  showFile = false;
  fileUploads: Observable<string[]>;
  data: string[];
  value: string;
  constructor(private uploadService: UploadFileService) { }

  ngOnInit() {
  }
  
  entered(event) {
    this.value = event.value;
  }

  showFiles(enable: boolean) {
    this.showFile = enable;

    if (enable) {
      this.fileUploads = this.uploadService.getFiles();
      this.fileUploads.subscribe(res => {
        console.log(res);
        this.data = res;
      })
      console.log(this.fileUploads);
      
    }
  }
}
