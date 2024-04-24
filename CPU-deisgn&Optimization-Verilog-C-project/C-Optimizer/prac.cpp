//
// Created by Apple on 01/06/2023.
//


#include <iostream>
#include <fstream>
#include <cmath>

#define RGB_SIZE 3

struct Image {
    size_t width;
    size_t height;
    size_t depth;
};

struct Histogram {
    unsigned int i0_50;     //Bin 0-50
    unsigned int i51_101;   //Bin 51-101
    unsigned int i102_152;  //Bin 102-152
    unsigned int i153_203;  //Bin 153-203
    unsigned int i204_255;  //Bin 204-255

};

struct Pixel {
    int red;
    int green;
    int blue;
};

Pixel returnPixel (unsigned char **pictureArray, size_t i, size_t j){
    Pixel pixel;
    pixel.red = pictureArray[i][j];
    pixel.green = pictureArray[i][j+1];
    pixel.blue = pictureArray[i][j+2];

    return pixel;
}

unsigned int CountGray (Pixel pixel){
    return std::round(0.2126 * (pixel.red) + 0.7152 * (pixel.green) + 0.0722 * (pixel.blue));
}

//handles saturation, in case  <0, >0
int saturate (int k){
    if (k <= 0){
        return 255;
    }
    if (k >= 255){
        return 255;
    }
    return k;
}

void UpdateHistogram (Histogram *histogram, unsigned int i){
    if (i <= 50){
        ++(histogram->i0_50);
    }
    else if (i <= 101){
        ++(histogram->i51_101);
    }
    else if (i <= 152){
        ++(histogram->i102_152);
    }
    else if (i <= 203){
        ++(histogram->i153_203);
    }
    else {
        ++(histogram->i204_255);
    }
}

int main (int argc, char ** argv){
    struct timespec first, last;
    clock_gettime(CLOCK_REALTIME, &first);

    std::ifstream inputFile;
    Image rawData;
    Histogram histogram;
    histogram.i0_50 = histogram.i51_101 = histogram.i102_152 = histogram.i153_203 = histogram.i204_255 = 0;

    //Checking existence of file
    if(argc != 2){
        return -1;
    }
    inputFile.open(argv[1], std::ios::binary);

    //Read the header
    inputFile.seekg(3, std::ios::beg);
    inputFile >> rawData.width >> rawData.height >> rawData.depth;

    //Read the data and scan file
    unsigned char **pictureArray = static_cast<unsigned char **>(malloc(rawData.height * sizeof(unsigned char *)));
    for (size_t i = 0; i < rawData.height; i++){
        pictureArray[i] = static_cast<unsigned char *>(malloc(rawData.width * RGB_SIZE));
        if(!inputFile.read(reinterpret_cast<char *>(pictureArray[i]), rawData.width * RGB_SIZE)){
            return -1;
        }
    }
    inputFile.close();

    //write the header to our output.ppm
    std::ofstream  outputFile("output.ppm", std::ios::binary);
    outputFile << "P6\n" << rawData.width << "\n" << rawData.height << "\n" << rawData.depth << "\n";

    //Write the first line
    for(size_t i = 0; i < rawData.width * RGB_SIZE; i += 3){
        Pixel pixel = returnPixel(pictureArray, 0, i);
        UpdateHistogram(&histogram, CountGray(pixel));
        outputFile << static_cast<unsigned char>(pixel.red) << static_cast<unsigned char>(pixel.green) << static_cast<unsigned char>(pixel.blue);
    }

    //middle rows
    for (size_t i = 1; i < rawData.height - 1; ++i){
        //write first pixel
        outputFile << pictureArray[i][0] << pictureArray[i][1] << pictureArray[i][2];
        Pixel pixel = returnPixel(pictureArray, i, 0);
        UpdateHistogram(&histogram, CountGray(pixel));
        for (size_t j = 3; j < (rawData.width - 1) * RGB_SIZE; j += 3){
            Pixel centerPixel = returnPixel(pictureArray, i, j);
            Pixel upperPixel = returnPixel(pictureArray, i - 1, j);
            Pixel downPixel = returnPixel(pictureArray, i + 1, j);
            Pixel leftPixel = returnPixel(pictureArray, i, j - 3);
            Pixel rightPixel = returnPixel(pictureArray, i, j + 3);
            Pixel saturatedPixel;
            saturatedPixel.red = saturate(5 * centerPixel.red - (upperPixel.red + downPixel.red + leftPixel.red + rightPixel.red));
            saturatedPixel.green = saturate(5 * centerPixel.green - (upperPixel.green + downPixel.green + leftPixel.green + rightPixel.green));
            saturatedPixel.blue = saturate(5 * centerPixel.blue - (upperPixel.blue + downPixel.blue + leftPixel.blue + rightPixel.blue));

            //write new pixel values to output File
            outputFile << static_cast<unsigned char>(saturatedPixel.red) << static_cast<unsigned char>(saturatedPixel.green) << static_cast<unsigned char>(saturatedPixel.blue);
            UpdateHistogram(&histogram, CountGray(saturatedPixel));

        }
        //write the last pixel
        outputFile << pictureArray[i][(rawData.width - 1) * RGB_SIZE] << pictureArray[i][(rawData.width - 1) * RGB_SIZE + 1] << pictureArray[i][(rawData.width - 1) * RGB_SIZE + 2];
        pixel = returnPixel(pictureArray, i, (rawData.width - 1) * RGB_SIZE);
        UpdateHistogram(&histogram, CountGray(pixel));

    }

    //write the last row
    for (size_t i = 0; i < rawData.width * RGB_SIZE; i += 3){
        Pixel pixel = returnPixel(pictureArray, rawData.height - 1, i);
        UpdateHistogram(&histogram, CountGray(pixel));
        outputFile << static_cast<unsigned char>(pixel.red) << static_cast<unsigned  char>(pixel.green) << static_cast<unsigned char>(pixel.blue);
    }
    outputFile.close();

    //free allocated memory
    for (size_t i = 0; i < rawData.height; i++){
        free(pictureArray[i]);
    }
    free(pictureArray);

    //write histogram to output.txt file
    std::ofstream outputFileTxt("output.txt");
    outputFileTxt << histogram.i0_50 << " " << histogram.i51_101 << " " << histogram.i102_152 << " " << histogram.i153_203 << " " << histogram.i204_255;
    outputFileTxt.close();

    clock_gettime(CLOCK_REALTIME, &last);
    double accum = ((last.tv_sec - first.tv_sec) * 1000.0 + (last.tv_nsec - first.tv_nsec) / 1000000.0);
    printf("Time: %.6lf ms\n", accum);

    return 0;
}