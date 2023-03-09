import { Component, OnInit } from '@angular/core';
import { UploadFileService } from '../upload-file.service';
import { HttpResponse, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'form-upload',
  templateUrl: './form-upload.component.html',
  styleUrls: ['./form-upload.component.css']
})
export class FormUploadComponent implements OnInit {

  selectedFiles: FileList;
  currentFileUpload: File;
  value: string;
  progress: { percentage: number } = { percentage: 0 };  
  constructor(private uploadService: UploadFileService) { }

  ngOnInit() {
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }
  entered(event) {
    this.value = event.value;
  }
  upload() {
    this.progress.percentage = 0;
    this.currentFileUpload = this.selectedFiles.item(0);
    const ext =  this.currentFileUpload.name.split('.').pop();
    console.log(ext);
    let siz = this.currentFileUpload.size;
    siz = siz / 1024;  

    let c = 0;

    if ((ext === "png" || ext === "jpeg" || 
         ext === "PNG" || ext === "JPEG") && siz < 500) {
    this.uploadService.pushFileToStorage(this.currentFileUpload).subscribe(event => {
      c = c + 1;
      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round(100 * event.loaded / event.total);
      } 
      else if (event instanceof HttpResponse) {
        console.log('File is completely uploaded!');  
      }       
    },
    err => {
      alert('Connection error!! Try Again...');
      window.location.reload();
      this.currentFileUpload = undefined; 
    });
    this.selectedFiles = undefined;
    }

    else if (siz > 500) {
    this.currentFileUpload = undefined;
    this.selectedFiles = undefined;
    alert('Image size is greater than 500KB!!');
    window.location.reload();
    }

    else {
      this.currentFileUpload = undefined;
      this.selectedFiles = undefined;
      alert('Upload only png or jpeg format images!!');
      window.location.reload();
    }
}
} 
