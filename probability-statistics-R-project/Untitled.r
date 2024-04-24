install.packages("Sleuth2") #installing the package sleuth2 which we will use
install.packages("dplyr")
install.packages("DataExplorer")
install.packages("Hmisc")
install.packages("pastecs")
install.packages("UsingR")
install.packages("ggplot2")
install.packages("ggfortify")
install.packages("scales")
install.packages("plotly")
install.packages("pracma")
install.packages("fitdistrplus")
install.packages("lawstat")


library(Sleuth2) #importing the libraries from sleuth package
library(dplyr)
library(DataExplorer) #to generate a report file DataExplorer::create_report()
library(Hmisc) #for describe
library(pastecs) #for stat.desc
library(UsingR)
library(ggplot2)
library(ggfortify)
library(scales)
library(plotly)
library(pracma)
library(fitdistrplus)
library(lawstat)


##Task - 1
#Simple overview of our data for case0101
#case0101
??Sleuth2::case0101
View(mc) #to display the table
mc <- case0101
summary(mc)
dim(mc)
describe(mc)
stat.desc(mc)
attributes(mc)

#Displaying / Generating a report
DataExplorer::create_report(mc)
#Displaying estimation, variance and median of Intrinsic Treatment

In <- filter(mc, mc$Treatment == "Intrinsic")
View(In)

var(In$Score)
median(In$Score)
describe(In$Score)
stat.desc(In$Score)
summary(In$Score)

#Displaying estimation, variance and median of Extrinsic Treatment
ex <- filter(mc, mc$Treatment == "Extrinsic")
var(ex$Score)
median(ex$Score)
describe(ex$Score)
stat.desc(ex$Score)
summary(ex$Score)


##Task - 2
#histogram graph generating
hist(In$Score , col = "skyblue2", main = "Histogram of Intrinsic Treatment", ylab = "Intrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 8, probability = T)
lines(density(In$Score), col = "chocolate3")
plot(density(In$Score), frame = TRUE, col = "red", main = "Density of Histogram of Intrinsic Treatment", ylab = "Intrinsic Treatment Amount", xlab = "Score")

hist(ex$Score, col = "lightgreen", main = "Histogram of Extrinsic Treatment", ylab = "Extrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 8, probability = T)
lines(density(ex$Score), col = "orange3")
plot(density(ex$Score), frame = TRUE, col = "red", main = "Histogram of Extrinsic Treatment", ylab = "Extrinsic Treatment Amount", xlab = "Score")


#ecdf graph generating
In.ecdf = ecdf(In$Score)
ex.ecdf = ecdf(ex$Score)
In.ecdf
ex.ecdf

plot(In.ecdf, xlab = "Score", main = "Empirical Cumluative Distribution For Intrinsic Treatment", ylab = "Intrinsic Treatment amount")
  ggfortify::ggdistribution(In.ecdf, In$Score, colour = "black", alpha = 0.7, fill = "skyblue") + ggplot2::labs(title = "Cumulative Distribution Function for Intrinsic Treatment") + ggplot2::xlab ("Score") + ggplot2::ylab ("Frequency")
    
  
plot(ex.ecdf, xlab = "Score", main = "Empirical Cumluative Distribution For Extrinsic Treatment", ylab = "Extrinsic Treatment amount")
  ggfortify::ggdistribution(ex.ecdf, ex$Score, colour = "red", alpha = 0.7, fill = "skyblue") + ggplot2::labs(title = "Cumulative Distribution Function for Extrinsic Treatment", ) + ggplot2::xlab ("Score") + ggplot2::ylab ("Frequesncy")
#plot(density(In, weights = NULL, width = 3), main = "Density of group scores of Intrinsic", ylab = "Intrinsic Treatment amount", xlab = "Score")

  
  
#Task-3
mean_In <- mean(In$Score)
sd_In <- sqrt(var(In$Score))
a_b <- (mean(In$Score) * 2)
a__b <- (sqrt(var(In$Score) * 12))
a <- ((a_b - a__b) / 2)
b <- ((a_b + a__b) / 2)
In_x <- seq(min(In$Score), max(In$Score), length=100)
In_y_normal <- dnorm(In$Score, mean_In, sd_In)
In_y_expon <- dexp(In$Score, rate = 1/mean_In)
In_y_uniform <- dunif(In$Score, min = a, max = b, log = FALSE)

x <- hist(In$Score , col = "skyblue2", main = "Histogram of Intrinsic Treatment", ylab = "Intrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 8, probability = T, ylim = c(0, 0.2), xlim = c(10, 35))
legend("topright", seg.len = 2, c("Normal Distribution", "Exponential Distribution", "Uniform Distribution"), fill=c("red", "#336633", "#0033FF"))
lines(sum(In$Score)/24, type = "l", col = "gray", lwd = "3")
lines(In$Score, In_y_normal, type = "l", col = "red", lwd = "3")
lines(In$Score, In_y_expon, type = "l", col =c("#336633", "#0000FF"), lwd = "3")
first <-first(which(In_y_uniform != 0))
last <- last(which(In_y_uniform != 0))
lines(In$Score[first:last], In_y_uniform[first:last], col =c("#0033FF"), lwd = "3")

#lines(ex$Score[0:first], ex_y_uniform[0:first], col =c("#0033FF"), lwd = "3")
#We used sum of the indexes for uniform distribution as they are 0 in that range
ib_uniform = max(In$Score)
ib_exponen = 24/sum(In$Score)
ib_normal = sum(In$Score)/24
imin = min(abs(ib_uniform-ib_normal), abs(ib_normal-ib_normal), abs(ib_exponen-ib_normal))
#As ib_normal is close to the average of the data.So,
print("The normal distribution fits the data best.", imin)
#We can see that only normal distribution has the minimum distance


#Drawing histogram, normal-exponential-uniform distribution for Extrinsic
#First step is calculation of mean
mean_ex <- mean(ex$Score)
sd_ex <- sqrt(var(ex$Score))
a_b <- (mean(ex$Score) * 2)
a__b <- (sqrt(var(ex$Score) * 12))
a <- ((a_b - a__b) / 2)
b <- ((a_b + a__b) / 2)
ex_x <- seq(min(ex$Score), max(ex$Score), length=100)
ex_y_normal <- dnorm(ex$Score, mean_ex, sd_ex)
ex_y_expon <- dexp(ex$Score, rate = 1/mean_ex)
ex_y_uniform <- dunif(ex$Score, min = a, max = b, log = FALSE)

y <- hist(ex$Score, col = "lightgreen", main = "Histogram of Extrinsic Treatment", ylab = "Extrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 8, probability = T, ylim = c(0, .2), xlim = c(0, 30))
legend("topright", seg.len = 1, c("Normal Distribution", "Exponential Distribution", "Uniform Distribution"), fill=c("red", "#336633", "#0033FF"))
lines(ex$Score, ex_y_normal, type = "l", col = "red", lwd = "3")
lines(ex$Score, ex_y_expon, type = "l", col =c("#336633", "#0000FF"), lwd = "3")
first <-first(which(ex_y_uniform != 0))
last <- last(which(ex_y_uniform != 0))
lines(ex$Score[first:last], ex_y_uniform[first:last], col =c("#0033FF"), lwd = "3")

#lines(ex$Score[0:first], ex_y_uniform[0:first], col =c("#0033FF"), lwd = "3")
#We used sum of the indexes for uniform distribution as they are 0 in that range
eb_uniform = max(ex$Score)
eb_exponen = 23/sum(ex$Score)
eb_normal = sum(ex$Score)/23
emin = min(abs(eb_uniform-eb_normal), abs(eb_normal-eb_normal), abs(eb_exponen-eb_normal))
#As ib_normal is close to the average of the data.So,
print(paste("The normal distribution fits the data best with difference", emin))
#We can see that only normal distribution has the minimum distance



#Task--4
#We can use precalcuated parameters from  Task-3, mean and standard deviation
graph <- par(mfrow = c(2,2), cex = .4, mai = c(.3, .3, .3, .3))

In100 <- rnorm(100, mean_In, sd_In)
In_100 <- seq(min(In100), max(In100), length=100)
In_y_normal100 <- dnorm(In_100, mean_In, sd_In)
graph[1:1] <- hist(In100 , col = "blue", main = "New Histogram of Intrinsic Treatment", ylab = "Intrinsic Tratment Amount", xlab = "Score", plot = TRUE, breaks = 12, probability = T, ylim = c(0, 0.16))
lines(In_100, In_y_normal100, type = "l", col = "red", lwd = "3")
graph[1:2] <- hist(In$Score , col = "skyblue2", main = "Histogram of Intrinsic Treatment", ylab = "Intrinsic Tratment Amount", xlab = "Score", plot = TRUE, breaks = 12, probability = T, ylim = c(0, 0.16), xlim = c(10, 35))
lines(In$Score, In_y_normal, type = "l", col = "red", lwd = "3")

ex100 <- rnorm(100, mean_ex, sd_ex)
ex_100 <- seq(min(ex100), max(ex100), length=100)
ex_y_normal100 <- dnorm(ex_100, mean_ex, sd_ex)
graph[2:1] <- hist(ex100, col = "green", main = "New Histogram of Extrinsic Treatment", ylab = "Extrinsic Tratment Amount", xlab = "Score", plot = TRUE, breaks = 12, probability = T, ylim = c(0, .16), xlim = c(0, 30))
lines(ex_100, ex_y_normal100, type = "l", col = "red", lwd = "3")
graph[2:2] <- hist(ex$Score, col = "lightgreen", main = "Histogram of Extrinsic Treatment", ylab = "Extrinsic Tratment Amount", xlab = "Score", plot = TRUE, breaks = 12, probability = T, ylim = c(0, .16), xlim = c(0, 30))
lines(ex$Score, ex_y_normal, type = "l", col = "red", lwd = "3")


#Task-5
#first we need mean and standard deviation for In and ex dataframes.
In_lefttail = mean_In+(qt(.05/2, 23) * sd_In / sqrt(24))
In_righttail = mean_In-(qt(.05/2, 23) * sd_In / sqrt(24))
hist(In$Score , col = "skyblue2", main = "Confidence level interval for Intrinsic Treatment", ylab = "Intrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 7, probability = T, ylim = c(0, 0.2), xlim = c(10, 35))
abline(v = In_lefttail, col = "red", lwd = "2")
abline(v = In_righttail, col = "red", lwd = "2")
In_lefttail
In_righttail

ex_lefttail = mean_ex+(qt(.05/2, 22) * sd_ex / sqrt(23))
ex_righttail = mean_ex-(qt(.05/2, 22) * sd_ex / sqrt(23))
hist(ex$Score, col = "lightgreen", main = "Confidence level interval for Extrinsic Treatment", ylab = "Extrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 7, probability = T, ylim = c(0, .2), xlim = c(0, 30))
abline(v = ex_lefttail, col = "red", lwd = "2")
abline(v = ex_righttail, col = "red", lwd = "2")
ex_lefttail
ex_righttail


#Task-6
k = 15
if(k >= In_lefttail && k <= In_righttail){
  print('Null hypothesis for the group Intrinsic where the mean is equal to the value K, at the significance level 5% is not rejected')
}else{
  print('Null hypothesis for the group Intrinsic where the mean is equal to the value K, at the significance level 5% is rejected')
}
hist(In$Score , col = "skyblue2", main = "Confidence level interval for Intrinsic Treatment", ylab = "Intrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 7, probability = T, ylim = c(0, 0.2), xlim = c(10, 35))
lines(In$Score, In_y_normal, type = "l", col = "red", lwd = "3")
abline(v = In_lefttail, col = "red", lwd = "2")
abline(v = In_righttail, col = "red", lwd = "2")
abline(v = 15, col = "purple", lwd = "2")

if(k >= ex_lefttail && k <= ex_righttail){
  print('Null hypothesis for the group Extrinsic where the mean is equal to the value K, at the significance level 5% is not rejected')
}else{
  print('Null hypothesis for the group Extrinsic where the mean is equal to the value K, at the significance level 5% is rejected')
}
hist(ex$Score, col = "lightgreen", main = "Confidence level interval for Extrinsic Treatment", ylab = "Extrinsic Treatment Amount", xlab = "Score", plot = TRUE, breaks = 7, probability = T, ylim = c(0, .2), xlim = c(0, 30))
lines(ex$Score, ex_y_normal, type = "l", col = "red", lwd = "3")
abline(v = ex_lefttail, col = "red", lwd = "2")
abline(v = ex_righttail, col = "red", lwd = "2")
abline(v = 15, col = "purple", lwd = "2")

#Task-7
t.test(x = ex$Score)
t.test(x = In$Score)
t.test(In$Score, ex$Score)

#For significant level 5% we know that 
cz <- c(169, 178, 179, 186, 191)
nor <- c(175, 182, 183, 189, 191, 192)
var(cz)
var(nor)
t.test(cz, nor, paired=F, alternative="two.sided", conf.level = .5)
var.test(cz, nor)
